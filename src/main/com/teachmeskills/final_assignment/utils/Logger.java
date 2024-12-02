package main.com.teachmeskills.final_assignment.utils;

import main.com.teachmeskills.final_assignment.constant.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    public static void logFileError(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.ERROR_LOGGER_FILE_NAME, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("General error: " + e.getMessage());
        }
    }
}
