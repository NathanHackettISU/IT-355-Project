import java.time.LocalDate;
import java.util.List;

public class UserInfo {

    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private String password; 
    private String ssn;
    private String pin;
    private List<Account> accounts;

    public UserInfo() {} // Required for Jackson library

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
        return accounts;
    }

    public boolean validatePassword(String inputPassword){
        return password.equals(inputPassword);
    }

    public boolean validatePin(String inputPin){
        return pin.equals(inputPin);
    }
}
