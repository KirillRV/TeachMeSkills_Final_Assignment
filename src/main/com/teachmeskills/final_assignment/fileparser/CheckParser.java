package main.com.teachmeskills.final_assignment.fileparser;

import main.com.teachmeskills.final_assignment.model.Check;
import java.util.List;

public class CheckParser extends BaseParser<Check> implements Parser<Check> {

    protected Check analyzeTextFromFile(List<String> parsedText) {

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
