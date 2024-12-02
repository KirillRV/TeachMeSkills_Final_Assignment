package main.com.teachmeskills.final_assignment.fileparser;

import main.com.teachmeskills.final_assignment.model.Invoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InvoiceParser implements Parser<Invoice> {

    @Override
    public List<Invoice> parseFiles(List<Path> files) {
        List<Invoice> invoices = new ArrayList<>();

        for (Path file : files) {
            try {
                List<String> lines = Files.readAllLines(file);
                lines.removeIf(String::isBlank);
                lines.replaceAll(String::trim);
                Invoice invoice = analyzeTextFromFile(lines);
                invoices.add(invoice);
            } catch (IOException e) {
                System.err.println("Error during file reading: " + file.getFileName());
            }
        }

        return invoices;
    }

    private static Invoice analyzeTextFromFile(List<String> parsedText) {

        double invoiceAmount = 0;

        for (String line : parsedText) {

            if (line.toLowerCase().replace(" ", "").startsWith("totalamount")) {
                String amount = line.toLowerCase().replace(" ", "").replace("totalamount", "").replace("$", "").replace(",", "").trim();
                invoiceAmount = Parser.findAmountInString(amount);
            }
        }

        return new Invoice(invoiceAmount);
    }
}
