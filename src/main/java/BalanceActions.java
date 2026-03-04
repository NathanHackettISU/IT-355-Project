// checking balance, deposits, withdraws
import java.util.List;
import java.util.Scanner;

public class BalanceActions {
    //rule MET04, methods have appropriate access levels
    private final Scanner scanner;
    private final fileWriting fileOperations = new fileWriting();

    public BalanceActions() {
                //MET05, constructor only initializes fields rather than calling overridable methods
        this.scanner = new Scanner(System.in);
    }

    public void showBalanceMenu(UserInfo user) {
        //checks whether user is null. Rule EXP01
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        List<Account> accounts = user.getAccounts();
        //checks whether account is null or empty. Rule EXP01
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        Account selectedAccount = selectAccount(accounts);
        //checks if selected acct is null before it is used. Rule EXP01
        if (selectedAccount == null) {
            return;
        }

        boolean running = true;

        while (running) {

            System.out.println("\n=== Balance Actions ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            //rule EXP03, since choice is a string,.equals is used internally to compare strings
            switch (choice) {

                case "1":
                    checkBalance(selectedAccount);
                    break;

                case "2":
                    deposit(selectedAccount, user);
                    break;

                case "3":
                    withdraw(selectedAccount, user);
                    break;

                case "4":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private Account selectAccount(List<Account> accounts) {

        System.out.println("\nSelect Account:");

        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". " + accounts.get(i).getAccountName());
        }

        System.out.print("Choice: ");
        String input = scanner.nextLine();

        try {
            int index = Integer.parseInt(input) - 1;

            if (index >= 0 && index < accounts.size()) {
                return accounts.get(index);
            } else {
                System.out.println("Invalid selection.");
                return null;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private void checkBalance(Account account) {
        System.out.println("Current Balance: $" + account.getBalance());
    }

    private void deposit(Account account, UserInfo user) {

        System.out.print("Enter deposit amount: ");
        String input = scanner.nextLine();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
<<<<<<< HEAD
            //VNA00,synchronization is used to make sure the updated value visible
=======
            
            boolean depositAccepted;
>>>>>>> origin/main
            synchronized (account) {
                depositAccepted = account.recordTransaction(amount, Transaction.TransactionType.DEPOSIT);
                if (depositAccepted){
                    account.setBalance(account.getBalance() + amount);
                }
            }

            if (!depositAccepted){
                return;
            }

            System.out.println("Deposit successful.");
            System.out.println("New Balance: $" + account.getBalance());
            fileOperations.saveUser(user);

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void withdraw(Account account, UserInfo user) {

        System.out.print("Enter withdrawal amount: ");
        String input = scanner.nextLine();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
<<<<<<< HEAD
            //VNA00,synchronization is used to make sure the updated value visible
=======

            boolean withdrawAccepted;
>>>>>>> origin/main
            synchronized (account) {

                if (amount > account.getBalance()) {
                    System.out.println("Insufficient funds.");
                    return;
                }

                withdrawAccepted  = account.recordTransaction(amount, Transaction.TransactionType.WITHDRAWAL);
                if (withdrawAccepted){
                    account.setBalance(account.getBalance() - amount);
                }
            }

            if (!withdrawAccepted){
                return;
            }

            System.out.println("Withdrawal successful.");
            System.out.println("New Balance: $" + account.getBalance());
            fileOperations.saveUser(user);

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }
}