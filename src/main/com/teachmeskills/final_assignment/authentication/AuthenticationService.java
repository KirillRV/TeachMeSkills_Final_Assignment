package main.com.teachmeskills.final_assignment.authentication;

import main.com.teachmeskills.final_assignment.exceptions.AuthenticationException;
import main.com.teachmeskills.final_assignment.authentication.User;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private Map<String, User> userDatabase = new HashMap<>();

    public AuthenticationService() {
        // Добавление тестового пользователя
        userDatabase.put("user@example.com", new User("user@example.com", "password123", TFAUtils.generateSecretKey()));
    }

    public boolean login(String username, String password) throws AuthenticationException {
        User user = userDatabase.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Неверный логин или пароль.");
        }
        return true;
    }

    public boolean verifyOTP(String username, String code) throws AuthenticationException {
        User user = userDatabase.get(username);
        if (user == null) {
            throw new AuthenticationException("Пользователь не найден.");
        }
        return code.equals(TFAUtils.getTOTPCode(user.getSecretKey()));
    }
}