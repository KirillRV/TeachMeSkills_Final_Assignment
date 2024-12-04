package main.com.teachmeskills.final_assignment.model;

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

    double checkAmount;

    public Check(double checkAmount) {
        this.checkAmount = checkAmount;
    }

    public double getCheckAmount() {
        return checkAmount;
    }
}
