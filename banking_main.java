import java.util.Scanner;

public class banking_main {

    private static int menuSelection = 0;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        LoginService auth = new LoginService();
        CreateAccount createAccount = new CreateAccount();
    
        boolean userLoggedIn = false;

        while (true) {

            if (!userLoggedIn) 
            {

                System.out.println("\nWelcome to 355 Banking app");
                System.out.println("1. Login");
                System.out.println("2. Forgot Password");
                System.out.println("3. Create Account");
                System.out.println("4. ADMIN: Jump to logged in menu");
                System.out.println("0. Quit");

                System.out.print("Entry: ");
                menuSelection = scanner.nextInt();
                scanner.nextLine();

                switch (menuSelection) {

                    case 0:
                        scanner.close();
                        return;

                    case 1:
                        int result = auth.handleLogin(scanner);

                        if (result == LoginService.LOGIN_SUCCESS) {
                            userLoggedIn = true;
                        }

                        if (result == LoginService.LOGIN_FORGOT) {
                            System.out.println("forgot password call");
                            LoginService.forgotPassword();
                        }
                        break;

                    case 2:
                        System.out.println("forgot password call");
                        LoginService.forgotPassword();
                        break;

                    case 3:
                        System.out.println("create account call");
                        int accountResult = createAccount.create();
                        break;
                    case 4:
                        System.out.println("ADMIN: Jumped to logged in menu");
                        userLoggedIn = true;
                }
            } 
            
            else {
                System.out.println("\n--- Account Menu ---");
                System.out.println("1. View Balance");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Create New  Account");
                System.out.println("5. View Loans");
                System.out.println("6. Log out");


                menuSelection = scanner.nextInt();
                scanner.nextLine();

                switch (menuSelection) {

                    case 1:
                        System.out.println("balance display here");
                        break;

                    case 2:
                        System.out.println("deposit");
                        break;

                    case 3:
                        System.out.println("withdraw");
                        break;

                    case 4:
                        System.out.println("create new account");
                        break;

                    case 5:
                        System.out.println("loan(s) menu");
                        break;

                    case 6:
                        userLoggedIn = false;
                        System.out.println("Logged out.");
                        break;
                }
            }
        }
    }
}