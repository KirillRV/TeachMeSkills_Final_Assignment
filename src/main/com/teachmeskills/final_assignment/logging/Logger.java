package main.com.teachmeskills.final_assignment.logging;

import main.com.teachmeskills.final_assignment.constant.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The {@code Logger} class is responsible for handling logging functionality within the application.
 * It provides methods to log error messages and informational messages into separate files with timestamped entries.
 * The log entries are formatted with a specific set of markers (e.g., [ERROR], [INFO], [WARNING]) to indicate their type,
 * and are written to their designated files.
 * <p>
 * Key functionalities of the {@code Logger} class include:
 * <ul>
 *     <li>Logging error messages to an error log file.</li>
 *     <li>Logging informational messages to an informational log file.</li>
 * </ul>
 * The log files are created in directories specified by constants from the {@link Constants} class.
 * If the process of creating directories or writing to files fails, appropriate error messages will be printed to {@code System.err}.
 * <p>
 * This class uses {@link SimpleDateFormat} to format timestamps and ensures thread-safe file writing.
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * Logger.logFileError("An unexpected error occurred");
 * Logger.logFileInfo(1, "Application started successfully");
 * }</pre>
 *
 * @author Rita A
 * @version 1.0
 */
public class Logger {

    private static final String[] LOGGER_MARKERS = {"[ERROR]", "[INFO]", "[WARNING]"};
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final Path ERROR_LOG_PATH = Paths.get(Constants.ERROR_LOGGER_FILE_PATH, "error_log.txt");
    private static final Path INFO_LOG_PATH = Paths.get(Constants.INFO_LOGGER_FILE_PATH, "info_log.txt");


    static {
        try {
            Files.createDirectories(ERROR_LOG_PATH.getParent());
            Files.createDirectories(INFO_LOG_PATH.getParent());
        } catch (IOException e) {
            System.err.println("Failed to initialize log directories: " + e.getMessage());
        }
    }

    public static void logFileError(String message) {
        log(ERROR_LOG_PATH, LOGGER_MARKERS[0], message);
    }

    /**
     * Logs a message to the informational log file.
     * @param loggerMarkerType Marker type: 0 - [ERROR], 1 - [INFO], 2 - [WARNING].
     * @param message The message to be logged.
     */
    public static void logFileInfo(int loggerMarkerType, String message) {
        log(INFO_LOG_PATH, LOGGER_MARKERS[loggerMarkerType], message);
    }

    public static void logFileInfo() {
        log(INFO_LOG_PATH,"","----------------------------------------------------------");
    }

    private static void log(Path logFile, String marker, String message) {
        String formattedDate = SIMPLE_DATE_FORMAT.format(new Date());
        String logMessage = marker + " " + formattedDate + " -> " + message;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile.toFile(), true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file (" + logFile + "): " + e.getMessage());
        }
    }
}