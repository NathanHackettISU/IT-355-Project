import java.io.*;
import java.security.SerializablePermission;

public class Loans implements Serializable {

    private String loanId;
    private double amount;

    public Loans(String loanId, double amount) {
        this.loanId = loanId;
        this.amount = amount;
    }

    //Rule 14 - SER01-J - Devin Diaz
    //Correct readObject signature for custom deserialization logic.
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (loanId == null) {
            throw new InvalidObjectException("Loan ID cannot be null.");
        }
    }

    //Rule 14 - SER04-J - Devin Diaz
    //Check security permissions before serialization.
    private void writeObject(ObjectOutputStream out) throws IOException {

        SecurityManager sm = System.getSecurityManager();

        if (sm != null) {
            sm.checkPermission(new SerializablePermission("enableSubstitution"));
        }

        out.defaultWriteObject();
    }

    //Rule 14 - SER05-J - Devin Diaz
    //Static nested classes are safe to serialize
    public static class LoanRecord implements Serializable {

        private String recordId;

        public LoanRecord(String id) {
            this.recordId = id;
        }
    }
}