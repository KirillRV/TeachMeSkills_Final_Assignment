package main.com.teachmeskills.final_assignment.model;

/**
 * Purchase Control
 * <p>
 * Boston Office
 * One Post Square 3600
 * Boston MA, 02109
 * USA
 * <p>
 * Order #P004950012
 * <p>
 * Order Total 6,570.09
 */
public class Order {

    String companyName;
    String orderNumber;
    String companyAddress;
    double orderAmount;

    public Order(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Order(String companyName, String orderNumber, String companyAddress, double orderAmount) {
        this.companyName = companyName;
        this.orderNumber = orderNumber;
        this.companyAddress = companyAddress;
        this.orderAmount = orderAmount;
    }

    public double getOrderAmount() {
        return orderAmount;
    }
}
