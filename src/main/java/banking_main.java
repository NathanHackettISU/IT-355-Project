import java.util.Scanner;
/*
* OBJ10-J "Don’t use public static nonfinal variables" is followed in this file, no public static nonfinal variables are present
* MET01-J "Don’t use assertions in the final code" is followed in this file, no assertations are present
*/
public class banking_main {

    private static int menuSelection = 0;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        LoginService auth = new LoginService();
        CreateAccount createAccount = new CreateAccount();
        BalanceActions balanceActions = new BalanceActions();
        BankingAccountCreation bankingAccountCreation = new BankingAccountCreation();
    
        boolean userLoggedIn = false;
        UserInfo currentUser = null;

        while (true) {

            if (!userLoggedIn) 
            {

                System.out.println("\nWelcome to 355 Banking app");
                System.out.println("1. Login");
                System.out.println("2. Forgot Password");
                System.out.println("3. Create Account");
                System.out.println("0. Quit");

                System.out.print("Entry: ");
                menuSelection = scanner.nextInt();
                scanner.nextLine();

                switch (menuSelection) {

                    case 0:
                        scanner.close();
                        return;

                    case 1:
                        currentUser = auth.handleLogin(scanner);

                        if (currentUser != null) {
                            userLoggedIn = true;
                        }

                        if (LoginService.forgotPassword()) {
                            System.out.println("forgot password call");
                            LoginService.forgotPassword(); // this can be handled in a seperate file
                        }
                        break;

                    case 2:
                        System.out.println("forgot password call");
                        LoginService.forgotPassword();
                        break;

                    case 3:
                        System.out.println("\n\n---- Creating Account ----\n\n");
                        UserInfo newUser = createAccount.create();
                        if (newUser != null){
                            userLoggedIn = true;
                            currentUser = newUser;
                        } 
                        else
                            menuSelection = 1; // will route to login - false because account is already created
                        break;
                }
            } 
            
            else {

                System.out.println("\n\nWelcome " + currentUser.getFirstName() + " " + currentUser.getLastName());

                System.out.println("--- Account Menu ---");
                System.out.println("1. Balance & Transactions");
                System.out.println("2. Create New Account");
                System.out.println("3. View Loans");
                System.out.println("4. Log out");



                menuSelection = scanner.nextInt();
                scanner.nextLine();

                switch (menuSelection) {

                    case 1:
                        //view balance actions
                        balanceActions.showBalanceMenu(currentUser);
                        break;

                    case 2:
                        //create new bank account
                        bankingAccountCreation.createNewAccount(currentUser);
                        break;
                    case 3:
                        System.out.println("loan(s) menu");
                        break;

                    case 4:
                        userLoggedIn = false;
                        System.out.println("Logged out.");
                        break;
                }
            }
        }
    }
}
