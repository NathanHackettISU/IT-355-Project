import java.time.LocalDateTime;
import java.util.Arrays;

public class Account {
    
    private String accountName;
    private double balance;
    private Transaction[] transactionHistory;
    private static final int MAX_TRANSACTIONS = 100;

    public Account() {
        this.transactionHistory = new Transaction[MAX_TRANSACTIONS];
    }

    public Account(String accountName, double balance){

        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be null or empty.");
        }

        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountName = accountName;
        this.balance = balance;
        this.transactionHistory = new Transaction[MAX_TRANSACTIONS];
    }

    public String getAccountName() {
        return accountName;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.balance = balance;
    }

    public Transaction[] getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(Transaction[] transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public synchronized boolean recordTransaction(double amount, Transaction.TransactionType type) {
        Transaction incoming = new Transaction(amount, type, LocalDateTime.now(), balance);

        if (isDuplicate(incoming)) {
            System.out.println("Duplicate transaction detected. Action rejected.");
            return false;
        }

        addTransaction(incoming);
        return true;
    }
        private boolean isDuplicate(Transaction incoming) {
        Object[] incomingSignature = incoming.toSignature();
        for (Transaction existing : transactionHistory) {
            if (existing == null) continue;
            Object[] existingSignature = existing.toSignature();
            if (Arrays.equals(incomingSignature, existingSignature)) {
                return true;
            }
        }
        return false;
    }

    private void addTransaction(Transaction transaction) {
        for (int i = 0; i < transactionHistory.length; i++) {
            if (transactionHistory[i] == null) {
                transactionHistory[i] = transaction;
                return;
            }
        }
        System.out.println("Transaction history is full.");
    }

    public void printHistory() {
        System.out.println("\n--- Transaction History: " + accountName + " ---");
        boolean hasTransactions = false;
        for (Transaction t : transactionHistory) {
            if (t != null) {
                System.out.println(t.getType() + " | $" + t.getAmount()
                    + " | Balance after: $" + t.getBalanceAfter()
                    + " | " + t.getTimestamp());
                hasTransactions = true;
            }
        }
        if (!hasTransactions) {
            System.out.println("No transactions on record.");
        }
    }
}
