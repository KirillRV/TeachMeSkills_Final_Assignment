package main.com.teachmeskills.final_assignment.service;

import main.com.teachmeskills.final_assignment.constant.Constants;
import main.com.teachmeskills.final_assignment.fabric.ParserFabric;
import main.com.teachmeskills.final_assignment.fileparser.*;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.*;
import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.com.teachmeskills.final_assignment.constant.Constants.*;

/**
 * The {@code FileService} class is designed to handle various file-related operations,
 * such as reading, writing, copying, and managing file content.
 * <p>
 * This utility class aims to simplify common file handling tasks by providing
 * reusable and efficient methods to work with files.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>File reading and writing capabilities</li>
 *   <li>File content manipulation</li>
 *   <li>Utilities for moving files</li>
 *   <li>Error handling for common file operations</li>
 * </ul>
 *
 * <p><strong>Usage:</strong></p>
 * Instantiate the class or use its static methods directly to perform operations
 * on files such as text processing or binary file manipulation, including moving incorrect files
 * to the corresponding folder.
 *
 *
 * <p>Note: Ensure proper exception handling while using the methods
 * provided by the {@code FileService} class to handle I/O-related issues.
 * Also, please, pay attention that nested folders are also considered for file processing,
 * but with specific names: invoices, bills and orders. Other folders will be ignored.
 * </p>
 *
 * @author Rita Amosava
 * @version 1.0
 */
public class FileService {

    public static void getFiles(String folderPath) {

        try {
            if (folderPath == null || folderPath.trim().replaceAll(" ", "").isEmpty() || !Files.exists(Paths.get(folderPath))) {
                Logger.logFileError("Invalid folder path provided: " + folderPath);
                return;
            }
        } catch (Exception e) {
            Logger.logFileError("Error during folder analyzing: " + e.getMessage() + Arrays.toString(e.getStackTrace()));
            return;
        }

        List<Path> txtFiles;
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath), 2)) {
            txtFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(FileService::isInAllowedDirectory)
                    .toList();
        } catch (IOException | RuntimeException e) {
            Logger.logFileError("Error during folder processing: " + e.getMessage() + Arrays.toString(e.getStackTrace()));
            return;
        }

        List<Path> validFiles = new ArrayList<>();

        if (!txtFiles.isEmpty()) {
            for (Path file : txtFiles) {
                Logger.logFileInfo(1, "File processing is started: " + file.getFileName());
                if (isValidFile(file)) {
                    validFiles.add(file);
                    Logger.logFileInfo(1, "File is added to further processing: " + file.getFileName());
                } else {
                    moveInvalidFile(file, "File format/year is not valid.");
                }
            }

            Map<String, List<Path>> filesMap = classifyFiles(validFiles);

            if (filesMap.containsKey("unknown")) {
                List<Path> unknownFiles = filesMap.remove("unknown");
                if (unknownFiles != null && !unknownFiles.isEmpty()) {
                    for (Path file : unknownFiles) {
                        moveInvalidFile(file, "Incorrect file name.");
                    }
                }
            }

            processFiles(filesMap);
        } else {
            System.out.println("No files found in the specified folder.");
            Logger.logFileInfo(1, "No files found in the specified folder.");
        }
    }

    public static void ensureDirectoryExists(String fileName) {
        Path filePath = Paths.get(fileName);

        try {
            Files.createDirectories(filePath);
            Logger.logFileInfo(1, "Directory " + filePath.getFileName() + " has been created.");
        } catch (IOException e) {
            Logger.logFileError("Error creating log directory: " + e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }

    private static boolean isValidFile(Path file) {
        String fileName = file.getFileName().toString();

        return fileName.endsWith(FILE_FORMAT) && fileName.contains(FILE_YEAR_TO_PARSE);
    }

    private static void moveInvalidFile(Path file, String reason) {
        try {
            ensureDirectoryExists(INVALID_FILES_FOLDER);
            Path targetDir = Paths.get(INVALID_FILES_FOLDER);

            Files.move(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            Logger.logFileInfo(2, file.getFileName() + " has been moved to "
                    + INVALID_FILES_FOLDER + " folder as invalid file. Reason: " + reason);
        } catch (IOException e) {
            Logger.logFileError("Error moving invalid file: " + e.getMessage());
        }
    }

    private static Map<String, List<Path>> classifyFiles(List<Path> files) {
        return files.stream()
                .collect(Collectors.groupingBy(file -> {
                    String fileName = file.getFileName().toString().toLowerCase();
                    if (fileName.contains("bill")) return "bill";
                    else if (fileName.contains("invoice")) return "invoice";
                    else if (fileName.contains("order")) return "order";
                    else return "unknown";
                }));
    }

    private static void processFiles(Map<String, List<Path>> categorizedFiles) {
        Map<String, Double> totalAmounts = new HashMap<>();

        for (Map.Entry<String, List<Path>> entry : categorizedFiles.entrySet()) {
            String category = entry.getKey();
            List<Path> files = entry.getValue();

            Parser<?> parser = ParserFabric.createParser(category);
            if (parser != null) {
                double totalAmount = processCategory(parser, category, files);
                totalAmounts.put(category, totalAmount);
            } else {
                Logger.logFileError("Parser not found for category: " + category);
            }
        }

        createStatistic(totalAmounts);
    }

    private static double processCategory(Parser<?> parser, String category, List<Path> files) {

        switch (category) {
            case "invoice" -> {

                Map<Invoice, Path> invoices = ((InvoiceParser) parser).parseFiles(files);

                invoices.forEach((invoice, file) -> {
                    if (invoice.getInvoiceAmount() <= 0) {
                        moveInvalidFile(file, "Invoice amount is not correct or not specified.");
                    }
                });

                return invoices.keySet()
                        .stream()
                        .mapToDouble(Invoice::getInvoiceAmount)
                        .sum();
            }
            case "bill" -> {
                Map<Check, Path> checks = ((CheckParser) parser).parseFiles(files);

                checks.forEach((check, file) -> {
                    if (check.getCheckAmount() <= 0) {
                        moveInvalidFile(file, "Check amount is not correct or not specified.");
                    }
                });

                return checks.keySet()
                        .stream()
                        .mapToDouble(Check::getCheckAmount)
                        .sum();
            }
            case "order" -> {
                Map<Order, Path> orders = ((OrderParser) parser).parseFiles(files);

                orders.forEach((order, file) -> {

                    if (order.getOrderAmount() <= 0) {
                        moveInvalidFile(file, "Order amount is not correct or not specified.");
                    }
                });


                return orders.keySet()
                        .stream()
                        .mapToDouble(Order::getOrderAmount)
                        .sum();
            }
            default -> {
                Logger.logFileError("Unknown category " + category);
                return 0;
            }
        }
    }

    private static void createStatistic(Map<String, Double> totalAmounts) {

        ensureDirectoryExists(Constants.PATH_TO_STATISTICS);
        Path statisticsDirectory = Paths.get(Constants.PATH_TO_STATISTICS);

        Path outputPath = Paths.get(statisticsDirectory.toString(), "total_statistics.txt");

        List<String> stats = new ArrayList<>();
        totalAmounts.forEach((category, totalAmount) -> {
                    String formattedValue = String.format("%.2f", totalAmount);
                    stats.add("Statistics Report for Category: " + category +
                            "\nTotal amount for " + category + " is " + formattedValue + "\n"
                    );
                }
        );
        try {
            Files.write(outputPath, stats);
            System.out.println("Statistic has been successfully created to path: " + outputPath);
            Logger.logFileInfo(1, "Statistic has been successfully created: "
                    + outputPath);
        } catch (IOException e) {
            Logger.logFileError("Error writing statistics " + e.getMessage());
        }
    }

    private static boolean isInAllowedDirectory(Path file) {
        Path parent = file.getParent();
        if (parent == null) {
            return false;
        }
        String parentFolderName = parent.getFileName().toString().toLowerCase();
        return ALLOWED_DIRECTORIES.contains(parentFolderName);
    }
}
