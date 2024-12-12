package main.com.teachmeskills.final_assignment.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    private final Properties properties = new Properties();


    public PropertiesLoader(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Properties file not found: " + filePath);
        }
        try (FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
        }
    }

    public int getSessionDuration() {
        String value = properties.getProperty("session.duration", "30");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid session duration in properties file. Using default: 30 minutes.");
            return 30;
        }
    }

    public static void main(String[] args) {
        try {
            // Укажите путь к вашему файлу session.properties
            String filePath = "session.properties";

            // Создаем объект PropertiesLoader
            PropertiesLoader loader = new PropertiesLoader(filePath);

            // Получаем значение длительности сессии
            int sessionDuration = loader.getSessionDuration();
            System.out.println("Session Duration: " + sessionDuration + " minutes");
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
        }
    }
}