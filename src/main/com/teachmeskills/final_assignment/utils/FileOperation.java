package main.com.teachmeskills.final_assignment.utils;

import main.com.teachmeskills.final_assignment.constant.Constants;
import main.com.teachmeskills.final_assignment.fabric.ParserFabric;
import main.com.teachmeskills.final_assignment.fileparser.CheckParser;
import main.com.teachmeskills.final_assignment.fileparser.InvoiceParser;
import main.com.teachmeskills.final_assignment.fileparser.OrderParser;
import main.com.teachmeskills.final_assignment.fileparser.Parser;
import main.com.teachmeskills.final_assignment.model.Check;
import main.com.teachmeskills.final_assignment.model.Invoice;
import main.com.teachmeskills.final_assignment.model.Order;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
                switch (category) {
                    case "invoice":
                        InvoiceParser invoiceParser = (InvoiceParser) parser;
                        List<Invoice> invoices = invoiceParser.parseFiles(files);
                        double totalInvoiceAmount = 0;
                        for (Invoice invoice : invoices) {
                            totalInvoiceAmount += invoice.getInvoiceAmount();
                        }
                        totalAmounts.put(category, totalInvoiceAmount);
                        break;

                    case "bill":
                        CheckParser checkParser = (CheckParser) parser;
                        List<Check> checks = checkParser.parseFiles(files);
                        double totalCheckAmount = 0;
                        for (Check check : checks) {
                            totalCheckAmount += check.getCheckAmount();
                        }
                        totalAmounts.put(category, totalCheckAmount);
                        break;

                    case "order":
                        OrderParser orderParser = (OrderParser) parser;
                        List<Order> orders = orderParser.parseFiles(files);
                        double totalOrderAmount = 0;
                        for (Order order : orders) {
                            totalOrderAmount += order.getOrderAmount();
                        }
                        totalAmounts.put(category, totalOrderAmount);
                        break;

                    default:
                        System.err.println("Unknown category: " + category);
                }
            } else {
                System.out.println("Parser not found for category: " + category);
            }
        }

        createStatistic(totalAmounts);
    }

    private static void createStatistic(Map<String, Double> totalAmounts) {

        Path statisticsDirectory = Paths.get(Constants.PATH_TO_STATISTICS);
        try {
            Files.createDirectories(statisticsDirectory);
        } catch (IOException e) {
            System.err.println("Error creating statistics directory: " + e.getMessage());
            return;
        }

        for (Map.Entry<String, Double> entry : totalAmounts.entrySet()) {
            String category = entry.getKey();
            double totalAmount = entry.getValue();
            String formattedValue = String.format("%.2f", totalAmount);
            List<String> stats = new ArrayList<>();
            stats.add("Statistics Report for Category: " + category);
            stats.add("Total Amount: " + formattedValue);

            Path outputPath = Paths.get(statisticsDirectory.toString(), category + "_statistics.txt");

            try {
                Files.write(outputPath, stats);
                System.out.println("Statistics saved to: " + outputPath);
            } catch (IOException e) {
                System.err.println("Error writing statistics for " + category + ": " + e.getMessage());
            }
        }
    }
}
