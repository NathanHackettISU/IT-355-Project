import java.util.Scanner;

public class LoginService {
    fileWriting fileOperations = new fileWriting();
    private int incorrectCount = 0;

    public UserInfo handleLogin(Scanner scanner) {

        while (incorrectCount < 5) {

            UserInfo user = login(scanner);

            if (user != null) {
                return user;
            }
        }

        System.out.println("\nFatal: too many incorrect passwords");
        System.exit(0);
        return null;
    }

    private UserInfo login(Scanner scanner) {

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        UserInfo user = fileOperations.loadUser(username, password);

        if (user != null) {
            incorrectCount = 0;
            System.out.println("\nLogin successful.\n");
            return user;
        }

        incorrectCount++;
        System.out.println("\nLogin failed.\n");

        if (incorrectCount == 3) {
            System.out.println("\nForgot password? (Y/N)\n");
            System.out.print("Entry: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("y")) {
                forgotPassword();
                incorrectCount = 0;
            }
        }
        return null;
    }

    public static boolean forgotPassword(){
        return false; //temp send false
    }
}