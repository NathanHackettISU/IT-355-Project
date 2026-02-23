public class UserInfo {

    private int accountId;
    private static String accountName;
    private static String password;
    private int ssn;
    private String email;
    private int pin;

    public UserInfo(int accountId, String accountName, 
                    String password, int ssn, 
                    String email, int pin) {

        this.accountId   = accountId;
        this.accountName = accountName;
        this.password    = password;
        this.ssn         = ssn;
        this.email       = email;
        this.pin         = pin;
    }

    public int getAccountId(){
        return accountId;
    }

    public static String getAccountName() {
        return accountName;
    }

    public String getEmail(){
        return email;
    }

    public static boolean validatePassword(String inputPassword){
        return password.equals(inputPassword);
    }

    public boolean validatePin(int pin){
        return this.pin == pin;
    }
}
