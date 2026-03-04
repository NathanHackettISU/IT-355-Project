    // case 4 should be here
    // user should be able to make a new account
    // determine the type of account, provide a banking account id, routing number, etc
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BankingAccountCreation {

    private final Scanner scanner;
    private final Random random;
    private final fileWriting fileOperations = new fileWriting();

    public BankingAccountCreation() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void createNewAccount(UserInfo user) {

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

        if (accounts == null) {
            accounts = new ArrayList<>();
        }

        synchronized (accounts) {
            accounts.add(newAccount);
        }

        fileOperations.saveUser(user);
        System.out.println("Account successfully created!");
        System.out.println("Account Name: " + newAccount.getAccountName());
        System.out.println("Starting Balance: $" + newAccount.getBalance());
    }

    private String generateAccountId() {
        return String.valueOf(100000 + random.nextInt(900000));
    }
}