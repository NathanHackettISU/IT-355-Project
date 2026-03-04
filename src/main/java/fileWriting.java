import java.time.LocalDate;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class fileWriting {
    private static final String USER_DIRECTORY = "users";
    // in here we will be able to write to a file to save
        // user information
            // see UserInfo file
            // save user's balance info
            // loan info
            // etc.

    public UserInfo accountCreationSuccess(String userId, String username, String firstName, String lastName, String email, LocalDate dob, String password, String ssn, String pin){
        try{
            UserInfo userInfo = new UserInfo(
                userId, username, firstName, lastName, email, dob, password, ssn, pin, new ArrayList<>());

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(USER_DIRECTORY + "/" + userId + ".json"), userInfo);
            return userInfo;
        } catch (IOException e){
            System.out.println("Issue writing user file");
            return null;
        }

    }

    public boolean duplicateAccountFile(String userId){
        File userFile = new File(USER_DIRECTORY + "/" + userId + ".json");
        return userFile.exists();
    }

    public UserInfo loadUser(String username, String password){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            File folder = new File("users");
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    UserInfo user = mapper.readValue(file, UserInfo.class);

                    if (user.getUsername().equals(username) && user.validatePassword(password)) {
                        return user;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //return false;
        }
        return null;
    }
}
