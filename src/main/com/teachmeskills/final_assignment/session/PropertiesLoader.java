package main.com.teachmeskills.final_assignment.session;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    private final Properties properties = new Properties();

    public PropertiesLoader(String filePath) throws IOException {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
    }

    public int getSessionDuration() {
        return Integer.parseInt(properties.getProperty("session.duration", "30")); // default 30 minutes
    }
}