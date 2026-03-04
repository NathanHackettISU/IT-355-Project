import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class CreateAccount {

    fileWriting fileOperations = new fileWriting();

    public UserInfo create(){
        Scanner userInput = new Scanner(System.in);
        DateTimeFormatter dateFormatting = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        String username = nonEmptyStringCheck(userInput, "Create a Username: ");
        String firstName = nonEmptyStringCheck(userInput, "Provide your first name: ");
        String lastName = nonEmptyStringCheck(userInput, "Provide your last name: ");
        LocalDate dob = nonEmptyDateCheck(userInput, dateFormatting);

        // Test here if the account was already craeted
        String userId = uniqueUserIdCreation(username, firstName, lastName, dob);
        if (userId == null){
            System.out.println("\nAccount already exists.\tRouting to login");
            return null;
        }

        String email = nonEmptyEmailCheck(userInput, "Provide a valid email: ");        
        String password = nonEmptyStringCheck(userInput, "Create a password: ");
        String ssn = nonEmptyPinSSNCheck(userInput, "Provide your social security number (9 digits): ", 9);
        String pin = nonEmptyPinSSNCheck(userInput, "Create a 4-digit PIN: ", 4);


        return fileOperations.accountCreationSuccess(userId, username, firstName, lastName, email, dob, password, ssn, pin);
    }

    private String uniqueUserIdCreation(String username, String firstName, String lastName, LocalDate dob){
        // pulling apart dob to create unqiue userId
        int month = dob.getMonthValue();
        int lastDigitMonth = month % 10;

        int day = dob.getDayOfMonth();
        int firstDigitDay = day / 10;

        int year = dob.getYear();
        int secondDigitYear = (year / 100) % 10;
        int lastDigitYear = year % 10;

        String uniqueDobPortion = "" + lastDigitMonth + firstDigitDay + secondDigitYear + lastDigitYear;
        String uniqueId = firstName + lastName + uniqueDobPortion + username;

        boolean duplicateStatus = fileOperations.duplicateAccountFile(uniqueId);
        if (!duplicateStatus){
            return uniqueId;
        }
        else
            return null;
    }

    private String nonEmptyStringCheck(Scanner scanner, String systemMessage){
        String input; 

        do {
            System.out.print(systemMessage);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Field cannot be empty.");
            }
        } while (input.isEmpty());

        return input;
    }

    private LocalDate nonEmptyDateCheck(Scanner scanner, DateTimeFormatter formatter) {
        LocalDate date = null;

        while (date == null) {
            System.out.print("Provide your date of birth (MM/dd/yyyy): ");
            String input = scanner.nextLine();

            try {
                date = LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        return date;
    }

    private String nonEmptyPinSSNCheck(Scanner scanner, String message, int length) {
        String input;

        do {
            System.out.print(message);
            input = scanner.nextLine().trim();

            if (!input.matches("\\d{" + length + "}")) {
                System.out.println("Input must be exactly " + length + " digits.");
                input = "";
            }

        } while (input.isEmpty());

        return input;
    }

    private String nonEmptyEmailCheck(Scanner scanner, String systemMessage){
        String input;

        do {
            System.out.print(systemMessage);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Field cannot be empty.");
            }
            if(!input.contains("@") || !input.contains(".")){
                System.out.println("Entered email is not valid");
            }

        } while (input.isEmpty() || !input.contains("@") || !input.contains("."));

        return input;
    }
}