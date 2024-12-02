package main.com.teachmeskills.final_assignment.model;

import java.util.Date;

/**
 * From: WorlWide ABC Company
 * Pay To: EU National Subcompany
 * Check # 01
 * Order #
 * Invoice #
 * Date: 15.01.2023
 * <p>
 * Bill total amount EURO 102,78
 */
public class Check {

    String companyFromName;
    String companyToName;
    String checkNumber;
    Date checkDate;
    double checkAmount;

    public Check(double checkAmount) {
        this.checkAmount = checkAmount;
    }

    public Check(double checkAmount, Date checkDate, String checkNumber, String companyToName, String companyFromName) {
        this.checkAmount = checkAmount;
        this.checkDate = checkDate;
        this.checkNumber = checkNumber;
        this.companyToName = companyToName;
        this.companyFromName = companyFromName;
    }

    public double getCheckAmount() {
        return checkAmount;
    }
}
