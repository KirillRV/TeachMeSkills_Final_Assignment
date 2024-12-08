package main.com.teachmeskills.final_assignment.fileparser.documentParser;

import main.com.teachmeskills.final_assignment.fileparser.BaseParser;
import main.com.teachmeskills.final_assignment.fileparser.Parser;
import main.com.teachmeskills.final_assignment.model.Order;
import java.util.List;

public class OrderParser extends BaseParser<Order> implements Parser<Order> {

    @Override
    protected Order analyzeTextFromFile(List<String> parsedText) {

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
