package main.com.teachmeskills.final_assignment.auntefication;
public class User {
    private String username;
    private String password;
    private String secretKey;

    public User(String username, String password, String secretKey) {
        this.username = username;
        this.password = password;
        this.secretKey = secretKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSecretKey() {
        return secretKey;
    }
}