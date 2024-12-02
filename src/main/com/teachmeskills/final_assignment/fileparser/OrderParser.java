package main.com.teachmeskills.final_assignment.fileparser;

import main.com.teachmeskills.final_assignment.model.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OrderParser implements Parser<Order> {

    @Override
    public List<Order> parseFiles(List<Path> files) {
        List<Order> orders = new ArrayList<>();

        for (Path file : files) {
            try {
                List<String> lines = Files.readAllLines(file);
                lines.removeIf(String::isBlank);
                lines.replaceAll(String::trim);
                Order order = analyzeTextFromFile(lines);
                orders.add(order);
            } catch (IOException e) {
                System.err.println("Error during file reading: " + file.getFileName());
            }
        }

        return orders;
    }

    private static Order analyzeTextFromFile(List<String> parsedText) {

        double orderAmount = 0;

        for (String line : parsedText) {
            if (line.toLowerCase().replace(" ", "").startsWith("ordertotal")) {
                String amount = line.toLowerCase().replace(" ", "").replace("ordertotal", "").replace("$", "").replace(",", "").trim();
                orderAmount = Parser.findAmountInString(amount);
            }
        }

        return new Order(orderAmount);
    }
}
