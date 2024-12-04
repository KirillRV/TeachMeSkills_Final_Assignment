package main.com.teachmeskills.final_assignment.logging;

import main.com.teachmeskills.final_assignment.constant.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import static main.com.teachmeskills.final_assignment.utils.FileOperation.ensureDirectoryExists;

public class Logger {

    public static void logFileError(String message) {
        ensureDirectoryExists(Constants.ERROR_LOGGER_FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.ERROR_LOGGER_FILE_PATH
                + "error_log.txt", true))) {
            writer.write(new Date() + " " + message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to error log file: " + e.getMessage());
        }
    }

    public static void logFileInfo(String message) {
        ensureDirectoryExists(Constants.INFO_LOGGER_FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.INFO_LOGGER_FILE_PATH
                + "info_log.txt", true))) {
            writer.write(new Date() + " " + message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to info log file: " + e.getMessage());
        }
    }
}

