    // case 2 should be here
    // user should be able to make a new account
    // determine the type of account, provide a banking account id, routing number, etc
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BankingAccountCreation {
    //rule MET04, methods have appropriate access levels
    private final Scanner scanner;
    private final Random random;

    public BankingAccountCreation() {
        //MET05, constructor only initializes fields rather than calling overridable methods
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void createNewAccount(UserInfo user) {
        //checks whether user is null. Rule EXP01
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\n=== Create New Account ===");

        System.out.println("Select Account Type:");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.print("Choice: ");

        String choice = scanner.nextLine();
        String accountType;

        //rule EXP03, uses .equals to compare instead of equality operators
        if ("1".equals(choice)) {
            accountType = "Checking";
        } else if ("2".equals(choice)) {
            accountType = "Savings";
        } else {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.print("Enter initial deposit amount: ");
        String depositInput = scanner.nextLine();

        double initialDeposit;

        try {
            initialDeposit = Double.parseDouble(depositInput);

            if (initialDeposit < 0) {
                System.out.println("Deposit cannot be negative.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid deposit amount.");
            return;
        }

        String generatedAccountName = accountType + "-" + generateAccountId();

        Account newAccount = new Account(generatedAccountName, initialDeposit);

        List<Account> accounts = user.getAccounts();
        //checks whether account is null before it is used. Rule EXP01
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        //VNA00,synchronization is used to make sure the updated value visible
        synchronized (accounts) {
            accounts.add(newAccount);
        }

        System.out.println("Account successfully created!");
        System.out.println("Account Name: " + newAccount.getAccountName());
        System.out.println("Starting Balance: $" + newAccount.getBalance());
    }

    private String generateAccountId() {
        return String.valueOf(100000 + random.nextInt(900000));
    }
}