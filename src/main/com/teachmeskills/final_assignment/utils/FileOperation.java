package main.com.teachmeskills.final_assignment.utils;

import main.com.teachmeskills.final_assignment.constant.Constants;
import main.com.teachmeskills.final_assignment.fabric.ParserFabric;
import main.com.teachmeskills.final_assignment.fileparser.*;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.CheckParser;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.InvoiceParser;
import main.com.teachmeskills.final_assignment.fileparser.documentParser.OrderParser;
import main.com.teachmeskills.final_assignment.model.Check;
import main.com.teachmeskills.final_assignment.model.Invoice;
import main.com.teachmeskills.final_assignment.model.Order;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileOperation {

    public static void getFiles(String folderPath) {
        List<Path> txtFiles;
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            txtFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .filter(path -> path.getFileName().toString().contains("2024"))
                    .collect(Collectors.toList());
        } catch (IOException | RuntimeException e) {
            System.out.println("Error during folder processing: " + e.getMessage());
            return;
        }

        Map<String, List<Path>> filesMap = classifyFiles(txtFiles);
        processFiles(filesMap);
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
                System.out.println("Parser not found for category: " + category);
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

        Path statisticsDirectory = Paths.get(Constants.PATH_TO_STATISTICS);
        try {
            Files.createDirectories(statisticsDirectory);
        } catch (IOException e) {
            System.err.println("Error creating statistics directory: " + e.getMessage());
            return;
        }

        totalAmounts.forEach((category, totalAmount) -> {
            String formattedValue = String.format("%.2f", totalAmount);
            List<String> stats = List.of(
                    "Statistics Report for Category: " + category,
                    "Total Amount: " + formattedValue
            );

            Path outputPath = Paths.get(statisticsDirectory.toString(), category + "_statistics.txt");

            try {
                Files.write(outputPath, stats);
                System.out.println("Statistics saved to: " + outputPath);
            } catch (IOException e) {
                System.err.println("Error writing statistics for " + category + ": " + e.getMessage());
            }
        });
    }
}
