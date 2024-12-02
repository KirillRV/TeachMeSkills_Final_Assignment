package main.com.teachmeskills.final_assignment.model;

import java.util.Date;
import java.util.List;

/**
 * INVOICE
 *
 * East Repair Inc.
 *
 * Invoice Date 09/10/2023
 * Due Date 26/10/2023
 *
 *
 * 1. Repair tools set 789-1 model 100
 * 2. Repair tools set 789-1 model 100
 * 3. Repair tools set 789-1 model 100
 * 4. Repair tools set 789-1 model 100
 * 5. Repair tools set 789-1 model 100
 * 6. Repair tools set 789-1 model 100
 *
 *
 * Total amount 600$
 */
public class Invoice {

    String companyName;
    Date invoiceDate;
    Date dueDate;
    List<String> serviceName;
    double invoiceAmount;

    public Invoice(double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Invoice(double invoiceAmount, List<String> serviceName, Date dueDate, Date invoiceDate, String companyName) {
        this.invoiceAmount = invoiceAmount;
        this.serviceName = serviceName;
        this.dueDate = dueDate;
        this.invoiceDate = invoiceDate;
        this.companyName = companyName;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }
}
