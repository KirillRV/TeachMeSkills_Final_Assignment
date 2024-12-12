package main.com.teachmeskills.final_assignment.storage;

import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import main.com.teachmeskills.final_assignment.model.user.User;

import java.util.HashMap;
import java.util.Map;

public class UserStorage {

    public static final Map<String, User> userDatabase = new HashMap<>();

    static {
        // Test user (example)
        userDatabase.put("user@example.com", new User("user@example.com", "password123", TFAUtils.generateSecretKey()));
    }
}
