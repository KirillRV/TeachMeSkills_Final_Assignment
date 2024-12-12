package main.com.teachmeskills.final_assignment.fileparser.documentParser;

import main.com.teachmeskills.final_assignment.fileparser.BaseParser;
import main.com.teachmeskills.final_assignment.fileparser.Parser;
import main.com.teachmeskills.final_assignment.model.document.Invoice;
import java.util.List;

public class InvoiceParser extends BaseParser<Invoice> implements Parser<Invoice> {

    @Override
    protected Invoice analyzeTextFromFile(List<String> parsedText) {

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
