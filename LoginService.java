import java.util.Scanner;

public class LoginService {

    private int incorrectCount = 0;

    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAILURE = 0;
    public static final int LOGIN_FORGOT = -1;

    public int handleLogin(Scanner scanner) {

        while (incorrectCount < 5) {

            int result = login(scanner);

            if (result == LOGIN_SUCCESS) {
                return LOGIN_SUCCESS;
            }

            if (result == LOGIN_FORGOT) {
                return LOGIN_FORGOT;
            }
        }

        System.out.println("\nFatal: too many incorrect passwords");
        System.exit(0);
        return -99;
    }

    private int login(Scanner scanner) {

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.equals(UserInfo.getAccountName()) &&
            UserInfo.validatePassword(password)) {

            incorrectCount = 0;
            System.out.println("\nLogin successful.\n");
            return LOGIN_SUCCESS;
        }

        incorrectCount++;
        System.out.println("\nLogin failed.\n");

        if (incorrectCount == 3) {
            System.out.println("\nForgot password? (Y/N)\n");
            System.out.print("Entry: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("y")) {
                return LOGIN_FORGOT;
            }
        }

        return LOGIN_FAILURE;
    }

    public static String forgotPassword(){
        return "forgot password method answer";
    }
}