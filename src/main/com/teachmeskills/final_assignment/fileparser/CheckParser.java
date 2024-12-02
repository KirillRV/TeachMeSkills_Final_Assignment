package main.com.teachmeskills.final_assignment.fileparser;

import main.com.teachmeskills.final_assignment.model.Check;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CheckParser implements Parser<Check> {

    @Override
    public List<Check> parseFiles(List<Path> files) {
        List<Check> checks = new ArrayList<>();

        for (Path file : files) {
            try {
                List<String> lines = Files.readAllLines(file);
                lines.removeIf(String::isBlank);
                lines.replaceAll(String::trim);
                Check check = analyzeTextFromFile(lines);
                checks.add(check);
            } catch (IOException e) {
                System.err.println("Error during file reading: " + file.getFileName());
            }
        }

        return checks;
    }

    private static Check analyzeTextFromFile(List<String> parsedText) {

        double checkAmount = 0;

        for (String line : parsedText) {
            if (line.toLowerCase().replace(" ", "").startsWith("billtotalamount")) {
                String amount = line.toLowerCase().replace(" ", "").replace("billtotalamounteuro", "").replace("$", "").trim();
                checkAmount = Parser.findAmountInString(amount);
            }
        }

        return new Check(checkAmount);
    }
}
