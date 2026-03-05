import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    /*
	 * OBJ10-J (Don’t use public static nonfinal variables) is followed in this file, no public static nonfinal variables are present
	 * MET01-J (Don’t use assertions in the final code) is followed in this file, no assertations are present
	 */
    // Rule 4 Start - MET11 - Joey Pina
        // username, userId, and dob have been made final since these cannot be reassigned after it's constructed. 
        // Since that these are the key fields for the object userId - if they were reassignable then comparison would break
        // like when we are seeing if their file / account already exists. 
    private final String userId;
    private final String username;
    private String firstName;
    private String lastName;
    private String email;
    private final LocalDate dob;
    private String password; 
    private String ssn;
    private String pin;
    private List<Account> accounts;

    // Required for Jackson library
    public UserInfo() {
        this.userId = null;
        this.username = null;
        this.dob = null;
    }

    // Rule 4 End - MET11 - Joey Pina

    public UserInfo(String userId, String username, 
                    String firstName, String lastName,
                    String email, LocalDate dob,
                    String password, String ssn, 
                    String pin, List<Account> accounts) {

        this.userId      = userId;
        this.username    = username;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.dob         = dob;
        this.password    = password;
        this.ssn         = ssn;
        this.pin         = pin;
        this.accounts    = accounts;
    }

    public String getUserId(){
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public LocalDate getDob(){
        return dob;
    }

    public String getPassword(){
        return password;
    }

    public String getSsn(){
        return ssn;
    }

    public String getPin(){
        return pin;
    }

    public List<Account> getAccounts(){
        //Rule 5, OBJ05-J Returns a clone of the object instead of a reference
        return new ArrayList<Account>(accounts);
        //Recommendation 6 - MET55-J - Devin Diaz
        //Return an empty collection instead of null
        if (accounts == null) {
            return new java.util.ArrayList<>();
        }
        return accounts;
    }

    public boolean validatePassword(String inputPassword){
        return password.equals(inputPassword);
    }

    public boolean validatePin(String inputPin){
        return pin.equals(inputPin);
    }
}
