// checking balance, deposits, withdraws
import java.util.List;
import java.util.Scanner;

public class BalanceActions {

    private final Scanner scanner;

    public BalanceActions() {
        this.scanner = new Scanner(System.in);
    }

    public void showBalanceMenu(UserInfo user) {

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        List<Account> accounts = user.getAccounts();

        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        Account selectedAccount = selectAccount(accounts);

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

            switch (choice) {

                case "1":
                    checkBalance(selectedAccount);
                    break;

                case "2":
                    deposit(selectedAccount);
                    break;

                case "3":
                    withdraw(selectedAccount);
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

    private void deposit(Account account) {

        System.out.print("Enter deposit amount: ");
        String input = scanner.nextLine();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            synchronized (account) {
                account.setBalance(account.getBalance() + amount);
            }

            System.out.println("Deposit successful.");
            System.out.println("New Balance: $" + account.getBalance());

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    private void withdraw(Account account) {

        System.out.print("Enter withdrawal amount: ");
        String input = scanner.nextLine();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            synchronized (account) {

                if (amount > account.getBalance()) {
                    System.out.println("Insufficient funds.");
                    return;
                }

                account.setBalance(account.getBalance() - amount);
            }

            System.out.println("Withdrawal successful.");
            System.out.println("New Balance: $" + account.getBalance());

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }
}