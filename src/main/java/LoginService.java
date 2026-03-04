import java.util.Scanner;
import java.util.logging.*;
/*
* OBJ10-J "Don’t use public static nonfinal variables" is followed in this file, no public static nonfinal variables are present
* MET01-J "Don’t use assertions in the final code" is followed in this file, no assertations are present
*/
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
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

        UserInfo user = null;

        // Rule 6 Start - ERR00 - Joey Pina
            // this rule ensures that the exception is not suppressed or 
            // ignored otherwise if continued the program will run in a unstable state

        try{
            user = fileOperations.loadUser(username, password);
        } catch (RuntimeException e){
            try{
                logger.log(Level.SEVERE, "Login system error for username=" + username + ": " + e.getMessage(), e);
            } catch (Exception loggingFailure){
                System.err.println("ERR02-J: Logging failure during login error handling.");
                System.err.println("ERR02-J: Original exception: " + e.getMessage());
            }
            System.out.println("A system error occurred during login. Please try again later.");
            return null;
        }

        // Rule 6 End - ERR00 - Joey Pina

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
