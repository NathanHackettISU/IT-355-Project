import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * Loans.java - Handles loan operations for the banking app.
 *
 * Lets users apply for a loan, view their current loans, make
 * payments on a loan, and see how much they still owe. Interest
 * is calculated as simple interest to keep things straightforward.
 *
 * @author Charles
 * @version 1.0
 */
public class Loans {

    /** the user's name tied to this loan account */
    private String accountHolder;

    /** list of all loans this user has */
    private List<Loan> loanList;

    /**
     * Inner class that represents a single loan.
     * Keeps track of the original amount, interest rate,
     * remaining balance, and monthly payment.
     */
    static class Loan {
        private String loanId;
        private String loanType;
        private double originalAmount;
        private double interestRate;
        private int termMonths;
        private double remainingBalance;
        private double monthlyPayment;

        /**
         * Creates a new loan with the given details. Calculates the
         * monthly payment automatically based on the loan amount,
         * interest rate, and term length.
         *
         * @param loanId         unique ID for this loan
         * @param loanType       what kind of loan (personal, auto, home)
         * @param amount         how much the loan is for
         * @param interestRate   annual interest rate as a percentage (e.g. 5.5)
         * @param termMonths     how many months to pay it off
         */
        public Loan(String loanId, String loanType, double amount,
                    double interestRate, int termMonths) {
            this.loanId = loanId;
            this.loanType = loanType;
            this.originalAmount = amount;
            this.interestRate = interestRate;
            this.termMonths = termMonths;
            this.remainingBalance = amount;
            this.monthlyPayment = calculateMonthlyPayment();
        }

        /**
         * Figures out the monthly payment using a standard loan formula.
         * If the interest rate is 0 we just divide evenly across months.
         *
         * @return the monthly payment amount
         */
        private double calculateMonthlyPayment() {
            if (interestRate == 0) {
                return originalAmount / termMonths;
            }
            // convert annual rate to monthly decimal
            double monthlyRate = (interestRate / 100.0) / 12.0;
            // standard amortization formula
            double payment = originalAmount
                    * (monthlyRate * Math.pow(1 + monthlyRate, termMonths))
                    / (Math.pow(1 + monthlyRate, termMonths) - 1);
            return Math.round(payment * 100.0) / 100.0;
        }

        /**
         * Makes a payment on this loan. Subtracts the payment from the
         * remaining balance. Won't let you pay more than what's owed.
         *
         * @param amount how much to pay
         * @return true if payment went through, false if something was wrong
         */
        public boolean makePayment(double amount) {
            if (amount <= 0) {
                System.out.println("    Payment amount has to be positive.");
                return false;
            }
            if (amount > remainingBalance) {
                System.out.println("    Payment of $" + String.format("%.2f", amount)
                        + " is more than what's owed ($"
                        + String.format("%.2f", remainingBalance) + ").");
                System.out.println("    Adjusting payment to remaining balance.");
                amount = remainingBalance;
            }
            remainingBalance -= amount;
            remainingBalance = Math.round(remainingBalance * 100.0) / 100.0;
            System.out.println("    Payment of $" + String.format("%.2f", amount)
                    + " applied to loan " + loanId);
            if (remainingBalance == 0) {
                System.out.println("    Loan " + loanId + " is fully paid off!");
            }
            return true;
        }

        /**
         * Checks if this loan is fully paid off.
         * @return true if nothing is owed
         */
        public boolean isPaidOff() {
            return remainingBalance <= 0;
        }

        /** @return the loan ID */
        public String getLoanId() { return loanId; }

        /** @return the loan type */
        public String getLoanType() { return loanType; }

        /** @return the original loan amount */
        public double getOriginalAmount() { return originalAmount; }

        /** @return the annual interest rate */
        public double getInterestRate() { return interestRate; }

        /** @return the term in months */
        public int getTermMonths() { return termMonths; }

        /** @return how much is left to pay */
        public double getRemainingBalance() { return remainingBalance; }

        /** @return the monthly payment amount */
        public double getMonthlyPayment() { return monthlyPayment; }

        @Override
        public String toString() {
            return "Loan " + loanId + " (" + loanType + ")"
                    + " | Original: $" + String.format("%.2f", originalAmount)
                    + " | Rate: " + interestRate + "%"
                    + " | Term: " + termMonths + " months"
                    + " | Monthly: $" + String.format("%.2f", monthlyPayment)
                    + " | Remaining: $" + String.format("%.2f", remainingBalance);
        }
    }

    /**
     * Creates a new Loans manager for the given account holder.
     *
     * @param accountHolder the name of the user
     */
    public Loans(String accountHolder) {
        this.accountHolder = accountHolder;
        this.loanList = new ArrayList<>();
    }

    /**
     * Walks the user through applying for a new loan. Asks for
     * the type, amount, and term, then creates the loan with a
     * fixed interest rate based on the loan type.
     *
     * @param scanner the scanner to read user input from
     */
    public void applyForLoan(Scanner scanner) {
        System.out.println("\n--- Apply for a Loan ---");
        System.out.println("Loan types: 1) Personal  2) Auto  3) Home");
        System.out.print("Choose loan type (1-3): ");

        int typeChoice;
        try {
            typeChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Going back to menu.");
            return;
        }

        String loanType;
        double interestRate;
        switch (typeChoice) {
            case 1:
                loanType = "Personal";
                interestRate = 8.5;
                break;
            case 2:
                loanType = "Auto";
                interestRate = 5.9;
                break;
            case 3:
                loanType = "Home";
                interestRate = 3.5;
                break;
            default:
                System.out.println("Invalid choice. Going back to menu.");
                return;
        }

        System.out.print("Loan amount: $");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Going back to menu.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount has to be positive. Going back to menu.");
            return;
        }

        System.out.print("Term (in months): ");
        int termMonths;
        try {
            termMonths = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid term. Going back to menu.");
            return;
        }
        if (termMonths <= 0) {
            System.out.println("Term has to be positive. Going back to menu.");
            return;
        }

        // generate a simple loan ID
        String loanId = "LN-" + (1000 + loanList.size() + 1);

        Loan newLoan = new Loan(loanId, loanType, amount, interestRate, termMonths);
        loanList.add(newLoan);

        System.out.println("\nLoan approved!");
        System.out.println("  Loan ID: " + loanId);
        System.out.println("  Type: " + loanType);
        System.out.println("  Amount: $" + String.format("%.2f", amount));
        System.out.println("  Interest Rate: " + interestRate + "%");
        System.out.println("  Term: " + termMonths + " months");
        System.out.println("  Monthly Payment: $" + String.format("%.2f", newLoan.getMonthlyPayment()));
    }

    /**
     * Prints out all the user's current loans with their details.
     * If they don't have any loans yet, it tells them that.
     */
    public void viewLoans() {
        System.out.println("\n--- Your Loans ---");
        if (loanList.isEmpty()) {
            System.out.println("You don't have any loans right now.");
            return;
        }
        for (int i = 0; i < loanList.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + loanList.get(i));
        }
    }

    /**
     * Lets the user make a payment on one of their existing loans.
     * Shows the loan list first, then asks which one and how much.
     *
     * @param scanner the scanner to read user input from
     */
    public void makePayment(Scanner scanner) {
        System.out.println("\n--- Make a Loan Payment ---");
        if (loanList.isEmpty()) {
            System.out.println("You don't have any loans to pay on.");
            return;
        }

        // show their loans so they can pick one
        for (int i = 0; i < loanList.size(); i++) {
            Loan loan = loanList.get(i);
            System.out.println("  " + (i + 1) + ". " + loan.getLoanId()
                    + " (" + loan.getLoanType() + ") - Remaining: $"
                    + String.format("%.2f", loan.getRemainingBalance()));
        }

        System.out.print("Which loan? (1-" + loanList.size() + "): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (choice < 1 || choice > loanList.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Loan selectedLoan = loanList.get(choice - 1);
        if (selectedLoan.isPaidOff()) {
            System.out.println("This loan is already paid off!");
            return;
        }

        System.out.println("  Suggested monthly payment: $"
                + String.format("%.2f", selectedLoan.getMonthlyPayment()));
        System.out.print("Payment amount: $");
        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        selectedLoan.makePayment(paymentAmount);
        System.out.println("  New remaining balance: $"
                + String.format("%.2f", selectedLoan.getRemainingBalance()));
    }

    /**
     * Returns how many active (not paid off) loans the user has.
     * @return number of active loans
     */
    public int getActiveLoanCount() {
        int count = 0;
        for (Loan loan : loanList) {
            if (!loan.isPaidOff()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the total amount owed across all active loans.
     * @return total remaining balance
     */
    public double getTotalOwed() {
        double total = 0;
        for (Loan loan : loanList) {
            total += loan.getRemainingBalance();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Shows a quick summary of the user's loan situation.
     */
    public void showSummary() {
        System.out.println("\n--- Loan Summary for " + accountHolder + " ---");
        System.out.println("  Total loans: " + loanList.size());
        System.out.println("  Active loans: " + getActiveLoanCount());
        System.out.println("  Total owed: $" + String.format("%.2f", getTotalOwed()));
    }

    /**
     * Runs the loan management menu. Loops until the user picks
     * "back to main menu." This is what banking_main would call
     * when the user selects the loans option.
     *
     * @param scanner the scanner to read user input from
     */
    public void loanMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n========== Loan Menu ==========");
            System.out.println("1. Apply for a loan");
            System.out.println("2. View my loans");
            System.out.println("3. Make a payment");
            System.out.println("4. Loan summary");
            System.out.println("5. Back to main menu");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    applyForLoan(scanner);
                    break;
                case "2":
                    viewLoans();
                    break;
                case "3":
                    makePayment(scanner);
                    break;
                case "4":
                    showSummary();
                    break;
                case "5":
                    running = false;
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Main method for testing the loan system by itself.
     * In the actual app, banking_main would create a Loans object
     * and call loanMenu() when the user picks that option.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        Loans loans = new Loans(name);
        loans.loanMenu(scanner);

        scanner.close();
    }
}