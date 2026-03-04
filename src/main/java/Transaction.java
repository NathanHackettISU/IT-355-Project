import java.time.LocalDateTime;
/*
* OBJ10-J "Don’t use public static nonfinal variables" is followed in this file, no public static nonfinal variables are present
* MET01-J "Don’t use assertions in the final code" is followed in this file, no assertations are present
*/
public class Transaction {

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }

    private double amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private double balanceAfter;

    public Transaction() {} // Required for Jackson

    public Transaction(double amount, TransactionType type, LocalDateTime timestamp, double balanceAfter) {
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
    }

    public double getAmount(){ 
        return amount; 
    }
    public TransactionType getType(){ 
        return type; 
    }
    public LocalDateTime getTimestamp(){
        return timestamp; 

    }
    public double getBalanceAfter(){
        return balanceAfter; 

    }

    public Object[] toSignature() {
        return new Object[]{ amount, type };
    }
}
