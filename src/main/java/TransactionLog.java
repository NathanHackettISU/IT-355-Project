import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * FIO14-J: Perform proper cleanup at program termination.
 *
 * <p><b>Description:</b> When a program exits abruptly — for example, when a
 * user presses Ctrl+C, the system sends a kill signal, or
 * {@code Runtime.halt()} is called — {@code finally} blocks may not execute.
 * This means resources like open files, database connections, and buffered
 * data could be left in a corrupted or incomplete state. The solution is to
 * register shutdown hooks via {@code Runtime.addShutdownHook()}, which the
 * JVM will attempt to run during graceful shutdown. Shutdown hooks should be
 * short, non-blocking, and must not depend on user interaction.</p>
 *
 * <p><b>Why is this rule important?</b> In a banking application, transaction
 * records are critical financial data subject to audit and regulatory
 * requirements. If the application buffers transactions in memory before
 * writing them to a log file, an abrupt shutdown means those buffered records
 * are lost permanently. This could result in missing deposits, unrecorded
 * withdrawals, or discrepancies in account balances. Shutdown hooks ensure
 * that even during an unexpected termination, pending transaction records
 * are flushed to persistent storage and temporary files containing sensitive
 * data are properly removed.</p>
 *
 * <p><b>Rule Category:</b> Rule 13 - Input Output (FIO)</p>
 * <p><b>Priority:</b> P6, Level: L2</p>
 * <p><b>Severity:</b> Medium | <b>Likelihood:</b> Likely</p>
 *
 * @author Charles
 * @version 1.0
 * @see <a href="https://wiki.sei.cmu.edu/confluence/display/java/FIO14-J.+Perform+proper+cleanup+at+program+termination">
 *      SEI CERT FIO14-J</a>
 */
public class FIO14J_TransactionLog {

    /** Logger for recording application events. */
    private static final Logger logger = Logger.getLogger(FIO14J_TransactionLog.class.getName());

    /** Buffer holding pending transaction records not yet written to disk. */
    private static final List<String> pendingTransactions = new ArrayList<>();

    /** Path to the persistent transaction log file. */
    private static final String LOG_FILE = "transactions.log";

    /** Path to a temporary file used during transaction processing. */
    private static final String TEMP_FILE = "temp_batch_data.tmp";

    // ========================================================================
    // NONCOMPLIANT CODE EXAMPLE
    // ========================================================================

    /**
     * <b>Noncompliant:</b> Relies solely on a {@code finally} block to write
     * pending transactions to the log file when the application exits. If
     * the JVM is terminated abruptly by Ctrl+C, a kill signal, or a system
     * crash, the {@code finally} block may never execute. All buffered
     * transaction records are lost, and temporary files containing sensitive
     * batch data remain on disk.
     */
    public static void runTransactionLogNoncompliant() {
        InputStream configStream = null;
        try {
            // Simulate opening a configuration resource
            configStream = new FileInputStream("db_config.properties");

            // Simulate recording transactions during a session
            pendingTransactions.add("2026-03-01 10:15:00 | DEPOSIT  | ACC-1001 | +$500.00");
            pendingTransactions.add("2026-03-01 10:17:30 | WITHDRAW | ACC-1002 | -$200.00");
            pendingTransactions.add("2026-03-01 10:20:45 | TRANSFER | ACC-1001 -> ACC-1003 | $150.00");

            System.out.println("    Transactions buffered: " + pendingTransactions.size());
            System.out.println("    Application running... (Ctrl+C would lose all data)");

            // If Ctrl+C happens here, the finally block below may NOT run.
            // All three transactions above would be permanently lost.

        } catch (FileNotFoundException e) {
            System.out.println("    Config file not found (expected in demo).");
        } finally {
            // NONCOMPLIANT: this finally block is NOT guaranteed to run
            // during abrupt JVM termination (Ctrl+C, kill, halt).
            System.out.println("    [Finally] Attempting to write pending transactions...");
            flushTransactions();
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    logger.severe("Failed to close config stream.");
                }
            }
        }
    }

    // ========================================================================
    // COMPLIANT SOLUTION: Register a shutdown hook
    // ========================================================================

    /**
     * <b>Compliant:</b> Registers a JVM shutdown hook via
     * {@code Runtime.addShutdownHook()} that will execute even when the
     * program is terminated by Ctrl+C or a kill signal. The hook performs
     * three critical cleanup tasks: (1) flushes all pending transactions
     * from the in-memory buffer to the persistent log file, (2) deletes
     * temporary files that may contain sensitive batch processing data,
     * and (3) simulates closing the database connection. This approach
     * provides reliable cleanup that does not depend on {@code finally}
     * blocks executing.
     */
    public static void runTransactionLogCompliant() {

        // COMPLIANT: register a shutdown hook for reliable cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n    [Shutdown Hook] JVM shutting down — starting cleanup...");

            // 1. Flush all pending transactions to the log file
            if (!pendingTransactions.isEmpty()) {
                System.out.println("    [Shutdown Hook] Writing "
                        + pendingTransactions.size() + " transactions to " + LOG_FILE);
                flushTransactions();
            } else {
                System.out.println("    [Shutdown Hook] No pending transactions.");
            }

            // 2. Remove temporary files with sensitive batch data
            java.io.File tempFile = new java.io.File(TEMP_FILE);
            if (tempFile.exists()) {
                if (tempFile.delete()) {
                    System.out.println("    [Shutdown Hook] Temp file deleted: " + TEMP_FILE);
                } else {
                    logger.warning("Failed to delete temp file: " + TEMP_FILE);
                }
            }

            // 3. Close database connection (simulated)
            System.out.println("    [Shutdown Hook] Database connection closed.");
            System.out.println("    [Shutdown Hook] Cleanup complete — no data lost.");
        }));

        // Simulate recording transactions during a session
        pendingTransactions.add("2026-03-01 10:15:00 | DEPOSIT  | ACC-1001 | +$500.00");
        pendingTransactions.add("2026-03-01 10:17:30 | WITHDRAW | ACC-1002 | -$200.00");
        pendingTransactions.add("2026-03-01 10:20:45 | TRANSFER | ACC-1001 -> ACC-1003 | $150.00");

        System.out.println("    Transactions buffered: " + pendingTransactions.size());
        System.out.println("    Application running with shutdown hook registered.");
        System.out.println("    Even Ctrl+C will trigger the hook and save all data.");
    }

    /**
     * Writes all pending transactions from the buffer to the log file.
     * Each transaction is appended on a new line. The buffer is cleared
     * after a successful write.
     */
    private static void flushTransactions() {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            for (String transaction : pendingTransactions) {
                writer.write(transaction + System.lineSeparator());
            }
            writer.flush();
            System.out.println("    Transactions written to " + LOG_FILE);
            pendingTransactions.clear();
        } catch (IOException e) {
            logger.severe("CRITICAL: Failed to write transactions — " + e.getMessage());
        }
    }

    /**
     * Demonstrates the FIO14-J rule with both noncompliant and compliant
     * examples in a banking transaction log context.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== FIO14-J: Perform proper cleanup at program termination ===\n");

        System.out.println("--- Noncompliant (relies only on finally block) ---");
        runTransactionLogNoncompliant();

        // Clear buffer for second demo
        pendingTransactions.clear();
        System.out.println();

        System.out.println("--- Compliant (uses Runtime shutdown hook) ---");
        runTransactionLogCompliant();

        System.out.println("\n    Exiting normally — shutdown hook will fire...");
        // The registered shutdown hook runs automatically when the JVM exits
    }
}