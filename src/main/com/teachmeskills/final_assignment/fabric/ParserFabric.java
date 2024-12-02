package main.com.teachmeskills.final_assignment.fabric;

import main.com.teachmeskills.final_assignment.fileparser.CheckParser;
import main.com.teachmeskills.final_assignment.fileparser.InvoiceParser;
import main.com.teachmeskills.final_assignment.fileparser.OrderParser;
import main.com.teachmeskills.final_assignment.fileparser.Parser;


public class ParserFabric {

    public static Parser createParser(String category) {

        String formattedCategory = category.trim().toLowerCase();

        if (formattedCategory.contains("bill")) {
            return new CheckParser();
        } else if (formattedCategory.contains("invoice")) {
            return new InvoiceParser();
        } else if (formattedCategory.contains("order")) {
            return new OrderParser();
        } else {
            return null;
        }
    }
}
