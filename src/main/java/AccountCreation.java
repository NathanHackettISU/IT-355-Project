import java.util.logging.Logger;

/**
 * TSM01-J: Do not let the this reference escape during object construction.
 *
 * When a constructor is still running, the object isn't fully set up yet.
 * If you let 'this' leak out during construction - like by starting a
 * thread in the constructor and passing 'this' to it - that other thread
 * can try to use the object before all the fields are assigned. It might
 * see null values or zeros for fields that haven't been set yet.
 *
 * In a banking app, imagine we're creating an account and we start a
 * background verification thread right in the constructor. That thread
 * might try to read the account number or initial deposit, but those
 * fields haven't been assigned yet because the constructor is still
 * running. The verification could approve an account with a null account
 * number, or a transaction could go through against a $0 balance.
 *
 * The fix is simple: don't start threads or pass 'this' around in the
 * constructor. Do all that stuff in a separate method that gets called
 * AFTER the object is fully built.
 *
 * Priority: P4, Level: L3
 *
 * @author Charles
 * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/TSM01-J.+Do+not+let+the+this+reference+escape+during+object+construction">
 *      SEI CERT TSM01-J</a>
 */
public class TSM01J_AccountCreation {

    private static final Logger logger = Logger.getLogger(TSM01J_AccountCreation.class.getName());

    // =====================================================
    // NONCOMPLIANT - 'this' escapes via thread in constructor
    // =====================================================

    /**
     * Noncompliant: starts a verification thread inside the constructor.
     * The thread gets a reference to 'this' before all the fields are
     * set, so it might see null for accountNumber and 0.0 for the
     * initial deposit. This is a race condition.
     */
    static class AccountNoncompliant implements Runnable {

        private String holderName;
        private String accountNumber;
        private double initialDeposit;
        private String accountType;

        /**
         * Creates an account and immediately starts verifying it.
         * Problem is, the verification thread can run before we finish
         * setting up the fields below.
         *
         * @param holderName     who owns the account
         * @param accountNumber  the account number
         * @param initialDeposit how much they're putting in
         * @param accountType    checking or savings
         */
        public AccountNoncompliant(String holderName, String accountNumber,
                                   double initialDeposit, String accountType) {
            this.holderName = holderName;

            // BAD: we start a thread here and pass 'this' to it.
            // the fields below haven't been assigned yet!
            new Thread(this).start();

            // these get set AFTER the thread already started
            this.accountNumber = accountNumber;
            this.initialDeposit = initialDeposit;
            this.accountType = accountType;
        }

        /**
         * This runs on the new thread. Since the constructor might not
         * be done yet, some of these fields could be null or 0.
         */
        @Override
        public void run() {
            System.out.println("    [Noncompliant] Verification thread sees:");
            System.out.println("      Holder:  " + holderName);
            System.out.println("      Account: " + accountNumber);   // might be null!
            System.out.println("      Deposit: $" + initialDeposit); // might be 0.0!
            System.out.println("      Type:    " + accountType);     // might be null!

            if (accountNumber == null || initialDeposit <= 0) {
                System.out.println("      WARNING: data is incomplete!");
            }
        }
    }

    // =====================================================
    // COMPLIANT - thread started after construction
    // =====================================================

    /**
     * Compliant: all fields are set in the constructor, and no threads
     * are started until someone explicitly calls startVerification().
     * By the time the thread runs, every field is guaranteed to have
     * its correct value. We also make fields final so they can't be
     * changed after construction.
     */
    static final class AccountCompliant implements Runnable {

        private final String holderName;
        private final String accountNumber;
        private final double initialDeposit;
        private final String accountType;

        /**
         * Creates a fully initialized account. Does NOT start any
         * threads or do anything weird with 'this'.
         *
         * @param holderName     who owns the account
         * @param accountNumber  the account number
         * @param initialDeposit how much they're putting in
         * @param accountType    checking or savings
         */
        public AccountCompliant(String holderName, String accountNumber,
                                double initialDeposit, String accountType) {
            this.holderName = holderName;
            this.accountNumber = accountNumber;
            this.initialDeposit = initialDeposit;
            this.accountType = accountType;
            // GOOD: nothing else happens here. no threads, no leaking 'this'
        }

        /**
         * Call this AFTER the constructor is done. It starts the
         * background verification thread. At this point all fields
         * are guaranteed to be fully initialized.
         */
        public void startVerification() {
            new Thread(this).start();
        }

        /**
         * Runs on the background thread. Everything is properly
         * initialized because we waited until after construction
         * to start this thread.
         */
        @Override
        public void run() {
            System.out.println("    [Compliant] Verification thread sees:");
            System.out.println("      Holder:  " + holderName);
            System.out.println("      Account: " + accountNumber);
            System.out.println("      Deposit: $" + initialDeposit);
            System.out.println("      Type:    " + accountType);

            if (accountNumber != null && initialDeposit > 0) {
                System.out.println("      Account verified successfully!");
            }
        }

        /** @return the holder's name */
        public String getHolderName() { return holderName; }

        /** @return the account number */
        public String getAccountNumber() { return accountNumber; }

        /** @return the initial deposit */
        public double getInitialDeposit() { return initialDeposit; }

        /** @return the account type */
        public String getAccountType() { return accountType; }
    }

    /**
     * Shows the difference between the two approaches. In the
     * noncompliant one, you'll likely see null/0 values. In the
     * compliant one, everything shows up correctly.
     *
     * @param args not used
     * @throws InterruptedException if sleep gets interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TSM01-J: Do not let 'this' escape during construction ===\n");

        System.out.println("--- Noncompliant (thread started in constructor) ---");
        AccountNoncompliant nc = new AccountNoncompliant(
                "John Doe", "ACC-9876", 5000.00, "CHECKING");
        Thread.sleep(500);

        System.out.println();

        System.out.println("--- Compliant (thread started after construction) ---");
        // step 1: build the object completely
        AccountCompliant c = new AccountCompliant(
                "Jane Smith", "ACC-5432", 12000.00, "SAVINGS");
        // step 2: NOW start the verification thread
        c.startVerification();
        Thread.sleep(500);
    }
}