package main.com.teachmeskills.final_assignment;

import com.google.zxing.WriterException;
import main.com.teachmeskills.final_assignment.authentication.AuthenticationService;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import main.com.teachmeskills.final_assignment.exception.AuthenticationException;
import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.session.SessionManager;
import main.com.teachmeskills.final_assignment.utils.FileOperation;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MainRunner {
    private static final AuthenticationService authService = new AuthenticationService();
    private static final SessionManager sessionManager = new SessionManager(1); // Длительность сессии — 1 минута для тестов
    private static String username;
    private static String secretKey;
    private static final String SESSION_FILE = "session_data.txt";

    public static void main(String[] args) {
        Logger.logFileInfo(1, "Программа запущена.");
        Scanner scanner = new Scanner(System.in);

        try {
            // Загрузка данных сессии из файла
            loadSessionData();

            if (username != null && secretKey != null) {
                Logger.logFileInfo(1, "Сессия найдена для пользователя: " + username);
                if (sessionManager.isSessionActive(username)) {
                    // Если сессия активна
                    handleActiveSession(scanner);
                } else {
                    // Если сессия истекла
                    handleExpiredSession(scanner);
                }
            } else {
                // Если пользователь заходит впервые
                handleFirstLogin(scanner);
            }
        } catch (AuthenticationException e) {
            Logger.logFileError("Ошибка аутентификации: " + e.getMessage());
            System.out.println("Ошибка аутентификации. Проверьте логин и пароль.");
        } catch (Exception e) {
            Logger.logFileError("Ошибка: " + e.getMessage());
            System.out.println("Произошла ошибка. Попробуйте снова.");
        } finally {
            sessionManager.endSession(username); // Завершаем сессию при завершении программы
            scanner.close();
            Logger.logFileInfo(1, "Программа завершена.");
        }
    }

    private static void handleActiveSession(Scanner scanner) {
        if (validateOTP(scanner)) {
            Logger.logFileInfo(1, "Успешный вход для пользователя: " + username);
            processFolder(scanner);
        } else {
            Logger.logFileError("Неверный временный пароль.");
            System.out.println("Неверный временный пароль. Завершение программы.");
        }
    }

    private static void handleExpiredSession(Scanner scanner)  {
        System.out.println("Сессия истекла. Введите временный пароль для обновления:");
        if (validateOTP(scanner)) {
            Logger.logFileInfo(1, "Временный пароль подтверждён. Сессия обновлена для пользователя: " + username);
            sessionManager.createSession(username); // Обновляем сессию
            saveSessionData(); // Сохраняем данные сессии
            processFolder(scanner);
        } else {
            Logger.logFileError("Неверный временный пароль.");
            System.out.println("Неверный временный пароль. Завершение программы.");
        }
    }

    private static void handleFirstLogin(Scanner scanner) throws IOException, WriterException, AuthenticationException {
        System.out.println("Введите логин:");
        username = scanner.nextLine();

        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        if (authService.login(username, password)) {
            Logger.logFileInfo(1, "Успешная аутентификация для пользователя: " + username);
            System.out.println("Успешный вход!");

            // Генерация секретного ключа и QR-кода
            secretKey = TFAUtils.generateSecretKey();
            String qrCodeData = TFAUtils.getGoogleAuthenticatorBarCode(secretKey, username, "TeachMeSkills");
            TFAUtils.createQRCode(qrCodeData, "qr_code.png", 400, 400);
            Logger.logFileInfo(1, "QR-код создан для пользователя: " + username);
            System.out.println("QR-код создан. Отсканируйте его и введите временный пароль.");
            if (validateOTP(scanner)) {
                Logger.logFileInfo(1, "Временный пароль подтверждён для пользователя: " + username);
                sessionManager.createSession(username); // Активируем сессию
                saveSessionData(); // Сохраняем данные сессии
                processFolder(scanner);
            } else {
                Logger.logFileError("Неверный временный пароль.");
                System.out.println("Неверный временный пароль. Завершение программы.");
            }
        } else {
            throw new AuthenticationException("Неверный логин или пароль.");
        }
    }

    private static boolean validateOTP(Scanner scanner) {
        System.out.println("Введите временный пароль:");
        String tempCode = scanner.nextLine();
        return TFAUtils.getTOTPCode(secretKey).equals(tempCode);
    }

    private static void processFolder(Scanner scanner) {
        System.out.println("Введите путь до папки с файлами:");
        String folderPath = scanner.nextLine();
        FileOperation.getFiles(folderPath);
        Logger.logFileInfo(1, "Обработка файлов завершена.");
    }

    private static void loadSessionData() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            Logger.logFileInfo(1, "Файл сессии отсутствует. Первый вход.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            username = reader.readLine();
            secretKey = reader.readLine();
            LocalDateTime expiryTime = LocalDateTime.parse(reader.readLine());
            sessionManager.createSession(username, expiryTime);
        } catch (IOException e) {
            Logger.logFileError("Ошибка чтения данных сессии: " + e.getMessage());
        }
    }

    private static void saveSessionData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username + "\n");
            writer.write(secretKey + "\n");
            writer.write(sessionManager.getSessionExpiry(username).toString() + "\n");
            Logger.logFileInfo(1, "Данные сессии сохранены.");
        } catch (IOException e) {
            Logger.logFileError("Ошибка сохранения данных сессии: " + e.getMessage());
        }
    }
}
