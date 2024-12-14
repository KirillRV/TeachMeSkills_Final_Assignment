package main.com.teachmeskills.final_assignment.service;

import com.google.zxing.WriterException;
import main.com.teachmeskills.final_assignment.exception.AuthenticationException;
import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.model.session.Session;
import main.com.teachmeskills.final_assignment.model.user.User;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import main.com.teachmeskills.final_assignment.session.SessionManager;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

import static main.com.teachmeskills.final_assignment.constant.Constants.QR_CODE_FILE_PATH;
import static main.com.teachmeskills.final_assignment.constant.Constants.SESSION_FILE;
import static main.com.teachmeskills.final_assignment.storage.UserStorage.userDatabase;


/**
 * The AuthenticationService class implements user authentication functionality
 * using two-factor authentication (2FA). It provides methods for:
 * <ul>
 *     <li>Processing user authentication.</li>
 *     <li>Managing active user sessions.</li>
 *     <li>Validating one-time passwords (OTP) during login.</li>
 *     <li>Generating QR codes for setting up two-factor authentication.</li>
 *     <li>Saving and loading session data to and from a file.</li>
 * </ul>
 * <p>
 * Key components of the class:
 * <ul>
 *     <li>User database (userDatabase): Contains demo user data for testing purposes.</li>
 *     <li>Authentication methods: Handles login and session processing, saves session data,
 *         generates QR codes, and validates OTP codes.</li>
 *     <li>Integration with utility classes and session management, particularly TFAUtils and SessionManager.</li>
 * </ul>
 * <p>
 * This class demonstrates examples of:
 * <ul>
 *     <li>Implementing security protocols using two-factor authentication.</li>
 *     <li>File operations to save and retrieve session data.</li>
 *     <li>Working with external libraries (e.g., Google ZXing).</li>
 * </ul>
 *
 * <p><b>Note:</b> This class includes examples and mock functionality for demonstration purposes.
 *
 * @author Kirill R. and Vlad K.
 */
public class AuthenticationService {

    public static void processAuthentication() {
        try (Scanner scanner = new Scanner(System.in)) {
            Session currentSession = loadSessionData();

            if (currentSession != null && SessionManager.isSessionActive(currentSession.getUsername())) {
                handleActiveSession(scanner, currentSession);
            } else {
                handleLoginProcess(scanner);
            }
        } catch (Exception e) {
            Logger.logFileError("Unexpected error: " + e.getMessage());
            System.out.println("An error occurred. Please try again.");
        } finally {
            Logger.logFileInfo(1, "Program ended.");
            Logger.logFileInfo();
        }
    }

    private static void handleActiveSession(Scanner scanner, Session session) {
        System.out.println("Session is active.");
        if (validateOTP(scanner, session.getSecretKey())) {
            Logger.logFileInfo(1, "Successful login for user: " + session.getUsername());
            FileService.processFolder(scanner);
        } else {
            Logger.logFileError("Invalid OTP. Program terminating.");
            System.out.println("Invalid OTP. Program will terminate.");
        }
    }

    private static void handleLoginProcess(Scanner scanner) {
        Session currentSession = loadSessionData();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            if (authenticateUser(username, password)) {
                User user = userDatabase.get(username);
                if (currentSession == null) {
                    generateQRCodeForUser(username, user);
                }

                if (SessionManager.getSession(username) == null) {
                    if (validateOTP(scanner, user.getSecretKey())) {
                        Logger.logFileInfo(1, "Authentication successful for user: " + username);
                        SessionManager.createSession(username, user.getSecretKey());
                        saveSessionData(username, user.getSecretKey());
                        FileService.processFolder(scanner);
                    } else {
                        throw new AuthenticationException("Invalid OTP.");
                    }
                } else if (SessionManager.getSession(username) != null && SessionManager.getSession(username).isExpired()) {
                    if (validateOTP(scanner, currentSession.getSecretKey())) {
                        Logger.logFileInfo(1, "Authentication successful for user: " + username);
                        SessionManager.removeSession(username);
                        SessionManager.createSession(username, user.getSecretKey());
                        saveSessionData(username, user.getSecretKey());
                        FileService.processFolder(scanner);
                    } else {
                        throw new AuthenticationException("Invalid OTP.");
                    }
                }
            }
        } catch (IOException | WriterException | AuthenticationException | NullPointerException e) {
            Logger.logFileError("Error during login: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private static boolean authenticateUser(String username, String password) throws AuthenticationException {
        User user = userDatabase.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            Logger.logFileError("Invalid username or password.");
            throw new AuthenticationException("Invalid username or password.");
        }
        return true;
    }

    private static boolean validateOTP(Scanner scanner, String secretKey) {
        System.out.print("Enter the OTP: ");
        String inputCode = scanner.nextLine();
        return TFAUtils.getTOTPCode(secretKey).equals(inputCode);
    }

    private static void generateQRCodeForUser(String username, User user) throws WriterException, IOException {
        String qrCodeData = TFAUtils.getGoogleAuthenticatorBarCode(user.getSecretKey(), username, "TeachMeSkills");
        TFAUtils.createQRCode(qrCodeData, QR_CODE_FILE_PATH, 400, 400);
        System.out.println("Scan the QR code and enter the OTP.");
    }

    private static Session loadSessionData() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String username = reader.readLine();
            String secretKey = reader.readLine();
            String expiryTimeStr = reader.readLine();

            if (username != null && secretKey != null && expiryTimeStr != null) {
                LocalDateTime expiryTime = LocalDateTime.parse(expiryTimeStr);
                if (expiryTime.isAfter(LocalDateTime.now())) {
                    SessionManager.createSession(username, secretKey, (int) java.time.Duration.between(LocalDateTime.now(), expiryTime).toMinutes());
                    return SessionManager.getSession(username);
                } else if (expiryTime.isBefore(LocalDateTime.now())) {
                    SessionManager.createSession(username, secretKey, expiryTime);
                    return SessionManager.getSession(username);
                } else {
                    Logger.logFileError("Invalid session data.");
                }
            }
        } catch (IOException e) {
            Logger.logFileError("Error reading session file: " + e.getMessage());
        }
        return null;
    }

    private static void saveSessionData(String username, String secretKey) {
        Session session = SessionManager.getSession(username);
        if (session == null) {
            Logger.logFileError("Cannot save session: no active session for user " + username);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username + "\n" + secretKey + "\n" + session.getExpiryTime());
            Logger.logFileInfo(1, "Session data saved.");
        } catch (IOException e) {
            Logger.logFileError("Error saving session: " + e.getMessage());
        }
    }
}