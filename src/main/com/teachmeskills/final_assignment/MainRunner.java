package main.com.teachmeskills.final_assignment;

import main.com.teachmeskills.final_assignment.authentication.AuthenticationService;
import main.com.teachmeskills.final_assignment.authentication.TFAUtils;
import main.com.teachmeskills.final_assignment.session.SessionManager;
import main.com.teachmeskills.final_assignment.utils.FileOperation;
import main.com.teachmeskills.final_assignment.logging.Logger;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class MainRunner {

    private static final AuthenticationService authService = new AuthenticationService();
    private static final SessionManager sessionManager = new SessionManager(30); // Сессия на 30 минут
    private static boolean isAuthenticated = false;
    private static boolean isFirstLogin = true;  // Переменная для отслеживания первого входа

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Генерация QR-кода для временного пароля
            String secretKey = TFAUtils.generateSecretKey();
            String qrCodeData = TFAUtils.getGoogleAuthenticatorBarCode(secretKey, "user@example.com", "TeachMeSkills");
            TFAUtils.createQRCode(qrCodeData, "qr_code.png", 400, 400);
            Logger.logFileInfo("QR-код создан: отсканируйте его с помощью программы для получения временного пароля.");

            // Основной цикл, проверяющий аутентификацию
            while (!isAuthenticated) {
                System.out.print("Введите временный пароль: ");
                String tempCode = scanner.nextLine();

                // Проверка временного пароля
                if (TFAUtils.getTOTPCode(secretKey).equals(tempCode)) {
                    Logger.logFileInfo("Временный пароль подтверждён!");

                    // Если это первый вход, выполняем аутентификацию
                    if (isFirstLogin) {
                        authenticateUser(scanner);
                    } else {
                        // Если сессия уже активна, пропускаем аутентификацию
                        Logger.logFileInfo("Сессия уже активна, пропускаем аутентификацию.");
                        isAuthenticated = true;
                    }
                } else {
                    Logger.logFileError("Неверный временный пароль.");
                    System.out.println("Неверный временный пароль. Попробуйте снова.");
                }
            }

            // После успешной аутентификации, переход к работе с файлами
            if (isAuthenticated) {
                String folderPath = getValidFolderPath(scanner);  // Проверка пути к папке

                if (isFolderEmpty(folderPath)) {
                    System.out.println("Папка пуста или не содержит доступных файлов для обработки.");
                } else {
                    FileOperation.getFiles(folderPath); // Передаем путь в метод обработки файлов
                }
            }

        } catch (WriterException | IOException e) {
            Logger.logFileError("Ошибка при работе с QR-кодом: " + e.getMessage());
        } catch (Exception e) {
            Logger.logFileError("Ошибка: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Генерация QR-кода
    private static void generateQRCode() {
        try {
            // Генерация QR-кода для временного пароля
            String secretKey = TFAUtils.generateSecretKey();
            String qrCodeData = TFAUtils.getGoogleAuthenticatorBarCode(secretKey, "user@example.com", "TeachMeSkills");
            TFAUtils.createQRCode(qrCodeData, "qr_code.png", 200, 200);
            Logger.logFileInfo("QR-код создан: отсканируйте его с помощью программы для получения временного пароля.");
        } catch (WriterException | IOException e) {
            Logger.logFileError("Ошибка при работе с QR-кодом: " + e.getMessage());
            System.out.println("Ошибка при создании QR-кода. Пожалуйста, попробуйте снова.");
        }
    }

    // Проверка пути до папки
    private static String getValidFolderPath(Scanner scanner) {
        String folderPath = null;
        while (folderPath == null || !Files.isDirectory(Paths.get(folderPath))) {
            System.out.print("Введите путь до папки с файлами: ");
            folderPath = scanner.nextLine();
            if (!Files.isDirectory(Paths.get(folderPath))) {
                Logger.logFileError("Путь не является действительной папкой. Попробуйте снова.");
                System.out.println("Ошибка: Путь не является действительной папкой. Попробуйте снова.");
            }
        }
        return folderPath;
    }

    // Проверка, пуста ли папка
    private static boolean isFolderEmpty(String folderPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            return paths.filter(Files::isRegularFile).count() == 0;
        } catch (IOException e) {
            Logger.logFileError("Ошибка при проверке содержимого папки: " + e.getMessage());
            return true; // Считаем, что папка пустая при ошибке
        }
    }

    // Проверка логина и пароля
    private static boolean isValidLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Логин или пароль не могут быть пустыми.");
            return false;
        }
        return true;
    }

    private static void authenticateUser(Scanner scanner) {
        try {
            System.out.print("Введите логин: ");
            String username = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            if (!isValidLogin(username, password)) {
                return;
            }

            // Выполнение аутентификации через сервис
            if (authService.login(username, password)) {
                Logger.logFileInfo("Успешная аутентификация!");
                sessionManager.createSession(username); // Создание сессии для пользователя
                isAuthenticated = true; // Устанавливаем флаг успешной аутентификации
                isFirstLogin = false; // Изменяем флаг на второй вход
            } else {
                Logger.logFileError("Ошибка аутентификации.");
                System.out.println("Ошибка аутентификации.");
            }
        } catch (Exception e) {
            Logger.logFileError("Ошибка аутентификации: " + e.getMessage());
            System.out.println("Ошибка аутентификации: " + e.getMessage());
        }
    }
}
