import java.time.LocalDateTime;
import java.util.Arrays;

public class Account {
    
    private String accountName;
    private double balance;
    private Transaction[] transactionHistory;
    private static final int MAX_TRANSACTIONS = 100;

    //Rule 9 - LCK01-J - Devin Diaz
    //Private lock object prevents synchronization on reusable objects
    private final Object lock = new Object();

    public Account() {
        this.transactionHistory = new Transaction[MAX_TRANSACTIONS];
    }

    public Account(String accountName, double balance){
        
        //Rule 6 - MET00-J - Devin Diaz
        //Validate method arguments before using them
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

    public double getBalance() {
        synchronized(lock) {
            return balance;
        }
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        
        synchronized(lock) {
            this.balance = balance;
        }
    }

    public Transaction[] getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(Transaction[] transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public boolean recordTransaction(double amount, Transaction.TransactionType type) {
        synchronized(lock) {
            Transaction incoming = new Transaction(amount, type, LocalDateTime.now(), balance);

            if (isDuplicate(incoming)) {
                System.out.println("Duplicate transaction detected. Action rejected.");
                return false;
            }

            addTransaction(incoming);
            return true;
        }
    }
    
    private boolean isDuplicate(Transaction incoming) {
        Object[] incomingSignature = incoming.toSignature();
        for (Transaction existing : transactionHistory) {
            if (existing == null) 
                continue;
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
        for (Transaction transaction : transactionHistory) {
            if (transaction != null) {
                System.out.println(transaction.getType() + " | $" + transaction.getAmount()
                    + " | Balance after: $" + transaction.getBalanceAfter()
                    + " | " + transaction.getTimestamp());
                hasTransactions = true;
            }
        }
        if (!hasTransactions) {
            System.out.println("No transactions on record.");
        }
    }
}
