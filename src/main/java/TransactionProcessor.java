import java.util.logging.Logger;

/**
 * THI00-J: Do not invoke Thread.run().
 *
 * When you want to run code on a new thread, you have to call start(),
 * not run(). If you call run() directly, it just runs the code on whatever
 * thread you're already on - it does NOT create a new thread. Your program
 * will look like it works, but everything is actually happening one thing
 * at a time on the main thread instead of running in the background.
 *
 * In our banking app, we need background threads to handle things like
 * processing deposits and withdrawals without freezing the UI. If we
 * accidentally call run() instead of start(), the whole app locks up
 * while the transaction processes. The user can't do anything until
 * it finishes, which is a really bad experience and could even look
 * like the app crashed.
 *
 * Priority: P6, Level: L2
 *
 * @author Charles
 * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/THI00-J.+Do+not+invoke+Thread.run()">
 *      SEI CERT THI00-J</a>
 */
public class THI00J_TransactionProcessor implements Runnable {

    private static final Logger logger = Logger.getLogger(THI00J_TransactionProcessor.class.getName());

    /** which account we're processing for */
    private final String accountId;

    /** how much money is involved */
    private final double amount;

    /** deposit or withdrawal */
    private final String transactionType;

    /**
     * Creates a new transaction processor.
     *
     * @param accountId       the account number
     * @param amount          the dollar amount
     * @param transactionType DEPOSIT or WITHDRAWAL
     */
    public THI00J_TransactionProcessor(String accountId, double amount, String transactionType) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    /**
     * This is the actual work that gets done. It simulates a slow
     * database write with a 2 second sleep. The important thing is
     * WHICH thread this runs on - if we called run() directly, it
     * runs on main. If we called start(), it runs on a new thread.
     */
    @Override
    public void run() {
        logger.info("Processing " + transactionType + " of $" + amount
                + " for account " + accountId
                + " on thread: " + Thread.currentThread().getName());
        try {
            // pretend this is a slow database write
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Transaction interrupted for " + accountId);
        }
        logger.info("Done processing " + transactionType + " for " + accountId);
    }

    // =====================================================
    // NONCOMPLIANT - calling run() directly
    // =====================================================

    /**
     * Noncompliant: calls run() on the thread object. This does NOT
     * start a new thread - it just runs the method right here on the
     * main thread. So the main thread is stuck waiting 2 seconds for
     * the "background" processing to finish. Not actually background
     * at all.
     */
    public static void processTransactionNoncompliant() {
        System.out.println("  Main thread: " + Thread.currentThread().getName());

        THI00J_TransactionProcessor processor =
                new THI00J_TransactionProcessor("ACC-1001", 500.00, "DEPOSIT");

        Thread thread = new Thread(processor);

        // BAD: this runs on the current thread, not a new one
        thread.run();

        // this won't print until run() finishes (2 seconds later)
        System.out.println("  Main thread resumes (it was BLOCKED this whole time)");
    }

    // =====================================================
    // COMPLIANT - calling start() to make a real new thread
    // =====================================================

    /**
     * Compliant: calls start() which actually creates a brand new
     * thread and runs the code there. The main thread keeps going
     * immediately without waiting. This is how threading is supposed
     * to work.
     */
    public static void processTransactionCompliant() {
        System.out.println("  Main thread: " + Thread.currentThread().getName());

        THI00J_TransactionProcessor processor =
                new THI00J_TransactionProcessor("ACC-1001", 500.00, "DEPOSIT");

        Thread thread = new Thread(processor);

        // GOOD: this actually creates a new thread
        thread.start();

        // this prints right away because main thread isn't blocked
        System.out.println("  Main thread resumes immediately (not blocked!)");
    }

    /**
     * Runs both examples so you can see the timing difference.
     *
     * @param args not used
     * @throws InterruptedException if sleep gets interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== THI00-J: Do not invoke Thread.run() ===\n");

        System.out.println("--- Noncompliant (calling run() - blocks main thread) ---");
        processTransactionNoncompliant();

        System.out.println();
        Thread.sleep(500);

        System.out.println("--- Compliant (calling start() - runs in background) ---");
        processTransactionCompliant();

        // wait for the background thread to finish so output is clean
        Thread.sleep(3000);
    }
}