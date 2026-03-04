import java.util.logging.Logger;

/**
 * TSM03-J: Do not publish partially initialized objects.
 *
 * Even after a constructor finishes, there's no guarantee that other
 * threads will see the final values of the fields right away. The Java
 * Memory Model allows the JVM to reorder instructions, so another thread
 * might read the object's fields and still see the defaults (null, 0, false)
 * instead of what you actually assigned. This is called "unsafe publication."
 *
 * To safely share an object between threads you need to use one of these:
 * - volatile keyword on the shared reference
 * - final fields (the JMM guarantees these are visible after construction)
 * - synchronized blocks
 * - concurrent utilities like AtomicReference
 *
 * In a banking app, if we create an account object and share it with a
 * notification thread through a regular (non-volatile) field, that thread
 * could see a null account holder name or a $0 balance. The customer
 * might get an email saying "Dear null, your balance is $0.00." Or worse,
 * a transaction could go through against an account that appears empty.
 *
 * Priority: P4, Level: L3
 *
 * @author Charles
 * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/TSM03-J.+Do+not+publish+partially+initialized+objects">
 *      SEI CERT TSM03-J</a>
 */
public class TSM03J_BankAccount {

    private static final Logger logger = Logger.getLogger(TSM03J_BankAccount.class.getName());

    // =====================================================
    // NONCOMPLIANT - publishing through a plain static field
    // =====================================================

    /**
     * Noncompliant: shares the account object through a regular static
     * field (not volatile, not synchronized). Another thread reading
     * this field might see the object before its fields are initialized.
     * The JVM is allowed to reorder the field writes and the reference
     * assignment, so the reading thread could get nulls and zeros.
     */
    static class BankAccountNoncompliant {

        private String holderName;
        private double balance;
        private String accountNumber;

        /**
         * BAD: this is a plain static field, not volatile.
         * Other threads aren't guaranteed to see the final values.
         */
        public static BankAccountNoncompliant sharedAccount;

        /**
         * Creates the account and immediately publishes it through
         * the static field. This is unsafe because the JVM might
         * make the reference visible to other threads before the
         * field assignments are done.
         *
         * @param holderName    who owns it
         * @param accountNumber the account number
         * @param balance       starting balance
         */
        public BankAccountNoncompliant(String holderName, String accountNumber,
                                       double balance) {
            this.holderName = holderName;
            this.accountNumber = accountNumber;
            this.balance = balance;

            // BAD: publishing 'this' through a non-volatile field
            sharedAccount = this;
        }

        @Override
        public String toString() {
            return "Account[holder=" + holderName
                    + ", number=" + accountNumber
                    + ", balance=$" + balance + "]";
        }
    }

    // =====================================================
    // COMPLIANT - using volatile for safe publication
    // =====================================================

    /**
     * Compliant (volatile): the shared reference is volatile. The
     * volatile keyword creates a "happens-before" relationship, which
     * means all the field assignments that happened before we wrote to
     * the volatile variable are guaranteed to be visible to any thread
     * that reads it afterwards. So the reading thread always sees the
     * fully constructed object.
     */
    static class BankAccountVolatile {

        private String holderName;
        private double balance;
        private String accountNumber;

        /**
         * GOOD: volatile makes sure other threads see everything
         * that was written before this assignment.
         */
        public static volatile BankAccountVolatile sharedAccount;

        /**
         * Creates the account. We publish it separately (outside the
         * constructor) through the volatile field.
         *
         * @param holderName    who owns it
         * @param accountNumber the account number
         * @param balance       starting balance
         */
        public BankAccountVolatile(String holderName, String accountNumber,
                                   double balance) {
            this.holderName = holderName;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "Account[holder=" + holderName
                    + ", number=" + accountNumber
                    + ", balance=$" + balance + "]";
        }
    }

    // =====================================================
    // COMPLIANT - using final fields
    // =====================================================

    /**
     * Compliant (final fields): all fields are final. The JMM has a
     * special guarantee for final fields - once the constructor is done,
     * any thread that gets a reference to the object is guaranteed to
     * see the correct values for all final fields. No volatile or
     * synchronization needed. This is the easiest way to make an
     * object safe to share, but it only works if the object is
     * immutable (fields never change after construction).
     */
    static final class BankAccountImmutable {

        private final String holderName;
        private final double balance;
        private final String accountNumber;

        /**
         * All fields are final, so the JMM guarantees they're visible
         * to every thread after construction. No extra work needed.
         *
         * @param holderName    who owns it
         * @param accountNumber the account number
         * @param balance       starting balance
         */
        public BankAccountImmutable(String holderName, String accountNumber,
                                    double balance) {
            this.holderName = holderName;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        /** @return the holder name */
        public String getHolderName() { return holderName; }

        /** @return the account number */
        public String getAccountNumber() { return accountNumber; }

        /** @return the balance */
        public double getBalance() { return balance; }

        @Override
        public String toString() {
            return "Account[holder=" + holderName
                    + ", number=" + accountNumber
                    + ", balance=$" + balance + "]";
        }
    }

    /**
     * Demos all three approaches. The noncompliant one MIGHT show
     * correct values in a simple test (race conditions are tricky
     * to reproduce), but it's not guaranteed to be safe in a real
     * multi-threaded environment.
     *
     * @param args not used
     * @throws InterruptedException if sleep gets interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TSM03-J: Do not publish partially initialized objects ===\n");

        // noncompliant - plain static field
        System.out.println("--- Noncompliant: plain static field (not safe) ---");
        new Thread(() -> {
            new BankAccountNoncompliant("John Doe", "ACC-1111", 10000.00);
        }).start();
        Thread.sleep(100);
        System.out.println("    Reader sees: " + BankAccountNoncompliant.sharedAccount);

        System.out.println();

        // compliant - volatile field
        System.out.println("--- Compliant: volatile field (safe) ---");
        new Thread(() -> {
            BankAccountVolatile acct = new BankAccountVolatile(
                    "Jane Smith", "ACC-2222", 25000.00);
            // publish AFTER constructing, through the volatile field
            BankAccountVolatile.sharedAccount = acct;
        }).start();
        Thread.sleep(100);
        System.out.println("    Reader sees: " + BankAccountVolatile.sharedAccount);

        System.out.println();

        // compliant - final fields
        System.out.println("--- Compliant: final fields (always safe) ---");
        BankAccountImmutable immutable = new BankAccountImmutable(
                "Bob Johnson", "ACC-3333", 50000.00);
        System.out.println("    Reader sees: " + immutable);
    }
}