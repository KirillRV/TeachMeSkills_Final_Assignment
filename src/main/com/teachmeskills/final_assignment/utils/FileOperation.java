package main.com.teachmeskills.final_assignment.utils;

import main.com.teachmeskills.final_assignment.constant.Constants;
import main.com.teachmeskills.final_assignment.fabric.ParserFabric;
import main.com.teachmeskills.final_assignment.fileparser.*;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.CheckParser;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.InvoiceParser;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.OrderParser;
import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.model.Check;
import main.com.teachmeskills.final_assignment.model.Invoice;
import main.com.teachmeskills.final_assignment.model.Order;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.com.teachmeskills.final_assignment.constant.Constants.*;

public class FileOperation {

    public static void getFiles(String folderPath) {
        if (folderPath == null || folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
            Logger.logFileError("Invalid folder path provided: " + folderPath);
            return;
        }

        List<Path> txtFiles;
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            txtFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(FileOperation::isInAllowedDirectory)
                    .toList();
        } catch (IOException | RuntimeException e) {
            Logger.logFileError("Error during folder processing: " + e.getMessage());
            return;
        }

        List<Path> validFiles = new ArrayList<>();

        for (Path file : txtFiles) {
            if (isValidFile(file)) {
                validFiles.add(file);
            } else {
                moveInvalidFile(file);
            }
        }

        Map<String, List<Path>> filesMap = classifyFiles(validFiles);
        processFiles(filesMap);
    }


    public static void ensureDirectoryExists(String fileName) {
        Path filePath = Paths.get(fileName);

        try {
            Files.createDirectories(filePath);
        } catch (IOException e) {
            System.out.println("Error creating log directory: " + e.getMessage());
        }
    }

    private static boolean isValidFile(Path file) {
        String fileName = file.getFileName().toString();

        return fileName.endsWith("txt") && fileName.contains("2024");
    }

    private static void moveInvalidFile(Path file) {
        try {
            Path targetDir = Paths.get(INVALID_FILES_FOLDER);
            if (!Files.exists(targetDir)) {
                Files.createDirectory(targetDir);
            }
            Files.move(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            Logger.logFileInfo(file.getFileName().toString() + " has been moved to " + INVALID_FILES_FOLDER);
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
        return switch (category) {
            case "invoice" -> ((InvoiceParser) parser).parseFiles(files)
                    .stream()
                    .mapToDouble(Invoice::getInvoiceAmount)
                    .sum();
            case "bill" -> ((CheckParser) parser).parseFiles(files)
                    .stream()
                    .mapToDouble(Check::getCheckAmount)
                    .sum();
            case "order" -> ((OrderParser) parser).parseFiles(files)
                    .stream()
                    .mapToDouble(Order::getOrderAmount)
                    .sum();
            default -> {
                System.err.println("Unknown category: " + category);
                yield 0;
            }
        };
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
            Logger.logFileInfo("Statistic has been successfully created.");
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
