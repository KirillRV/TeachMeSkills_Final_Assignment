package main.com.teachmeskills.final_assignment;

import com.google.zxing.WriterException;
import main.com.teachmeskills.final_assignment.service.AuthenticationService;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import main.com.teachmeskills.final_assignment.exception.AuthenticationException;
import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.session.SessionManager;
import main.com.teachmeskills.final_assignment.service.FileService;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

// TODO Vlad - Привести в порядок класс `MainRunner` — переместить все ненужные методы в соответствующие сервисы (например, сессий, авторизации и т.д.). Оставить только один статический метод в блоке try-catch.

public class MainRunner {
    private static final AuthenticationService authService = new AuthenticationService();
    private static final SessionManager sessionManager = new SessionManager(1); // Session duration — 1 minute for testing
    private static String username;
    private static String secretKey;
    private static final String SESSION_FILE = "session_data.txt";

    public static void main(String[] args) {
        Logger.logFileInfo(1, "Program started.");
        Scanner scanner = new Scanner(System.in);

        try {
            // Loading session data from file
            loadSessionData();
            // Sending statistics to AWS

            if (username != null && secretKey != null) {
                Logger.logFileInfo(1, "Session found for user: " + username);
                if (sessionManager.isSessionActive(username)) {
                    // If session is active
                    handleActiveSession(scanner);
                } else {
                    // If session has expired
                    handleExpiredSession(scanner);
                }
            } else {
                // If the user is logging in for the first time
                handleFirstLogin(scanner);
            }
        } catch (AuthenticationException e) {
            Logger.logFileError("Authentication error: " + e.getMessage());
            System.out.println("Authentication error. Please check your username and password.");
        } catch (Exception e) {
            Logger.logFileError("Error: " + e.getMessage());
            System.out.println("An error occurred. Please try again.");
        } finally {
            sessionManager.endSession(username); // Ending the session when the program ends
            scanner.close();
            Logger.logFileInfo(1, "Program ended.");
        }
    }

    private static void handleActiveSession(Scanner scanner) {
        if (validateOTP(scanner)) {
            Logger.logFileInfo(1, "Successful login for user: " + username);
            processFolder(scanner);
        } else {
            Logger.logFileError("Invalid temporary password.");
            System.out.println("Invalid temporary password. Program will terminate.");
        }
    }

    private static void handleExpiredSession(Scanner scanner)  {
        System.out.println("The session has expired.");
        if (validateOTP(scanner)) {
            Logger.logFileInfo(1, "Temporary password confirmed. The session has been updated for user: " + username);
            sessionManager.createSession(username); // Updating session
            saveSessionData(); // Saving session data
            processFolder(scanner);
        } else {
            Logger.logFileError("Invalid temporary password.");
            System.out.println("Invalid temporary password. Program will terminate.");
        }
    }

    private static void handleFirstLogin(Scanner scanner) throws IOException, WriterException, AuthenticationException {
        System.out.println("Enter username:");
        username = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        if (authService.login(username, password)) {
            Logger.logFileInfo(1, "Successful authentication for user: " + username);
            System.out.println("Login successful!");

            // Generating a secret key and QR code
            secretKey = TFAUtils.generateSecretKey();
            String qrCodeData = TFAUtils.getGoogleAuthenticatorBarCode(secretKey, username, "TeachMeSkills");
            TFAUtils.createQRCode(qrCodeData, "qr_code.png", 400, 400);
            Logger.logFileInfo(1, "QR code created for user: " + username);
            System.out.println("QR code created. Scan it and enter the temporary password.");
            if (validateOTP(scanner)) {
                Logger.logFileInfo(1, "Temporary password confirmed for user: " + username);
                sessionManager.createSession(username); // Activating session
                saveSessionData(); // Saving session data
                processFolder(scanner);
            } else {
                Logger.logFileError("Invalid temporary password.");
                System.out.println("Invalid temporary password. Program will terminate.");
            }
        } else {
            throw new AuthenticationException("Invalid username or password.");
        }
    }

    private static boolean validateOTP(Scanner scanner) {
        System.out.println("Enter the temporary password:");
        String tempCode = scanner.nextLine();
        return TFAUtils.getTOTPCode(secretKey).equals(tempCode);
    }
    // TODO Vlad - Вызвать метод отправки статистики AWS здесь после вызова `getFiles` и проверить, что статистика существует.
    private static void processFolder(Scanner scanner) {
        System.out.println("Enter the path to the folder with files:");
        String folderPath = scanner.nextLine();
        FileService.getFiles(folderPath);
        Logger.logFileInfo(1, "File processing completed.");
    }

    private static void loadSessionData() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            Logger.logFileInfo(1, "Session file does not exist. First login.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            username = reader.readLine();
            secretKey = reader.readLine();
            LocalDateTime expiryTime = LocalDateTime.parse(reader.readLine());
            sessionManager.createSession(username, expiryTime);
        } catch (IOException e) {
            Logger.logFileError("Error reading session data: " + e.getMessage());
        }
    }

    private static void saveSessionData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username + "\n");
            writer.write(secretKey + "\n");
            writer.write(sessionManager.getSessionExpiry(username).toString() + "\n");
            Logger.logFileInfo(1, "Session data saved.");
        } catch (IOException e) {
            Logger.logFileError("Error saving session data: " + e.getMessage());
        }
    }
}