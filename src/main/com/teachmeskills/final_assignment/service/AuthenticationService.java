package main.com.teachmeskills.final_assignment.service;

import main.com.teachmeskills.final_assignment.exception.AuthenticationException;
import main.com.teachmeskills.final_assignment.authentication.User;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * The AuthenticationService class handles user authentication and verification processes.
 * It provides functionality for:
 * <ul>
 *     <li>User login with username and password validation</li>
 *     <li>Two-factor authentication (TFA) verification using a one-time password (OTP)</li>
 * </ul>
 *
 * This service includes:
 * <ul>
 *     <li>A basic, in-memory user database for storing user credentials and secret keys</li>
 *     <li>Methods for user login and OTP verification</li>
 * </ul>
 *
 * Exceptions:
 * <ul>
 *     <li>{@link main.com.teachmeskills.final_assignment.exception.AuthenticationException} is
 *         thrown when an authentication error occurs (e.g., invalid credentials or user not found)</li>
 * </ul>
 *
 * Usage example:
 * <pre>{@code
 * AuthenticationService authService = new AuthenticationService();
 * boolean loginSuccess = authService.login("user@example.com", "password123");
 * boolean isOTPValid = authService.verifyOTP("user@example.com", "123456");
 * }</pre>
 *
 * Note: This implementation is for demonstration purposes and should not be used
 * in production without proper security measures.
 * @author Kirill R.
 * @version 1.0
 */
// TODO Кирилл- пересмотреть методы, может лучше статические сделать + вынести в константы поля юзера
    // TODO Кирилл - решить с Владом на счет дублирования verifyOTP метода (в мейне это validateOTP)
public class AuthenticationService {
    private Map<String, User> userDatabase = new HashMap<>();

    public AuthenticationService() {
        // Adding a test user
        userDatabase.put("user@example.com", new User("user@example.com", "password123", TFAUtils.generateSecretKey()));
    }

    public boolean login(String username, String password) throws AuthenticationException {
        User user = userDatabase.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return true;
    }

    public boolean verifyOTP(String username, String code) throws AuthenticationException {
        User user = userDatabase.get(username);
        if (user == null) {
            throw new AuthenticationException("User not found.");
        }
        return code.equals(TFAUtils.getTOTPCode(user.getSecretKey()));
    }
}