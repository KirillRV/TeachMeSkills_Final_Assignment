package main.com.teachmeskills.final_assignment.util;

import main.com.teachmeskills.final_assignment.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static main.com.teachmeskills.final_assignment.constant.Constants.PROPERTIES_FILE_PATH;

public class PropertiesLoader {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = Files.newInputStream(Paths.get(PROPERTIES_FILE_PATH));
            properties.load(inputStream);
        } catch (IOException e) {
            Logger.logFileError("Properties file isn't found: " + e.getMessage());
        }
        return properties;
    }
}