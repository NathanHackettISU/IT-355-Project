import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * TPS04-J: Ensure ThreadLocal variables are reinitialized when using
 * thread pools.
 *
 * ThreadLocal lets each thread have its own private copy of a variable.
 * The problem comes up when you use thread pools, because threads get
 * recycled. If one task changes the ThreadLocal and then the thread gets
 * reused for another task, that second task will see the leftover value
 * from the first task instead of the default. You have to manually
 * reset it when each task finishes.
 *
 * This is a big deal in a banking app because we might store the
 * logged-in user's ID in a ThreadLocal. If Thread A handles a request
 * for an admin user and then Thread A gets recycled to handle a request
 * for a regular user, that regular user's request could accidentally
 * run with admin privileges. That's a major security problem - one
 * customer could end up seeing another customer's data.
 *
 * Priority: P4, Level: L3
 *
 * @author Charles
 * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/TPS04-J.+Ensure+ThreadLocal+variables+are+reinitialized+when+using+thread+pools">
 *      SEI CERT TPS04-J</a>
 */
public class TPS04J_UserSessionHandler {

    private static final Logger logger = Logger.getLogger(TPS04J_UserSessionHandler.class.getName());

    /**
     * ThreadLocal that holds the current user's ID for each thread.
     * Starts out as "NONE" meaning nobody is logged in.
     */
    private static final ThreadLocal<String> currentUserId =
            new ThreadLocal<String>() {
                @Override
                protected String initialValue() {
                    return "NONE";
                }
            };

    /**
     * Gets whoever is currently "logged in" on this thread.
     * @return the user ID for this thread
     */
    public static String getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * Sets the logged-in user for this thread.
     * @param userId the user to set
     */
    public static void setCurrentUserId(String userId) {
        currentUserId.set(userId);
    }

    /**
     * Clears the ThreadLocal back to default. This is the important
     * part - you NEED to call this when a task is done so the next
     * task on the same thread doesn't inherit stale data.
     */
    public static void clearCurrentUserId() {
        currentUserId.remove();
    }

    /**
     * Simulates handling a banking request. Just prints out what
     * thread is handling it and what user it thinks is logged in.
     *
     * @param action what the user is trying to do
     */
    public static void handleBankingRequest(String action) {
        System.out.println("    Thread [" + Thread.currentThread().getName()
                + "] handling '" + action + "' for user: " + getCurrentUserId());
    }

    // =====================================================
    // NONCOMPLIANT - never clearing ThreadLocal
    // =====================================================

    /**
     * Noncompliant: uses a thread pool but never resets the ThreadLocal
     * when tasks finish. Since we only have 2 threads in the pool,
     * threads WILL get reused. When Task 1 sets the user to "ADMIN-001"
     * and then its thread gets recycled for Task 3, Task 3 will think
     * the admin is logged in even though nobody set that.
     *
     * @throws InterruptedException if thread sync gets interrupted
     */
    public static void processRequestsNoncompliant() throws InterruptedException {
        // only 2 threads means they HAVE to be reused
        Executor pool = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(3);

        // Task 1: admin logs in
        pool.execute(() -> {
            setCurrentUserId("ADMIN-001");
            handleBankingRequest("View All Accounts");
            // BAD: we never clear the ThreadLocal here
            latch.countDown();
        });

        // small delay so Task 1 finishes first
        Thread.sleep(200);

        // Task 2: regular user - should see "NONE"
        pool.execute(() -> {
            // if this runs on the same thread as Task 1,
            // it will STILL see "ADMIN-001" instead of "NONE"
            handleBankingRequest("Check My Balance");
            latch.countDown();
        });

        // Task 3: another regular user
        pool.execute(() -> {
            handleBankingRequest("Transfer Funds");
            latch.countDown();
        });

        latch.await();
    }

    // =====================================================
    // COMPLIANT - clearing ThreadLocal in a finally block
    // =====================================================

    /**
     * Compliant: wraps each task in try-finally and calls remove()
     * on the ThreadLocal when the task is done. This way, even if
     * the thread gets recycled, the next task starts fresh with
     * the default "NONE" value. No accidental session leaking.
     *
     * @throws InterruptedException if thread sync gets interrupted
     */
    public static void processRequestsCompliant() throws InterruptedException {
        Executor pool = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(3);

        // Task 1: admin logs in, cleans up after
        pool.execute(() -> {
            try {
                setCurrentUserId("ADMIN-001");
                handleBankingRequest("View All Accounts");
            } finally {
                // GOOD: always clear it when we're done
                clearCurrentUserId();
                latch.countDown();
            }
        });

        Thread.sleep(200);

        // Task 2: guaranteed to see "NONE" now
        pool.execute(() -> {
            try {
                handleBankingRequest("Check My Balance");
            } finally {
                clearCurrentUserId();
                latch.countDown();
            }
        });

        // Task 3: also safe
        pool.execute(() -> {
            try {
                handleBankingRequest("Transfer Funds");
            } finally {
                clearCurrentUserId();
                latch.countDown();
            }
        });

        latch.await();
    }

    /**
     * Runs both examples. Watch the thread names and user IDs in the
     * output - in the noncompliant version you'll probably see a
     * regular user task running with the admin's ID.
     *
     * @param args not used
     * @throws InterruptedException if thread sync gets interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TPS04-J: Reinitialize ThreadLocal in thread pools ===\n");

        System.out.println("--- Noncompliant (ThreadLocal never cleared) ---");
        processRequestsNoncompliant();

        Thread.sleep(1000);
        System.out.println();

        System.out.println("--- Compliant (ThreadLocal cleared in finally) ---");
        processRequestsCompliant();

        Thread.sleep(500);
        System.exit(0);
    }
}