package pl.edu.pjwstk.byt;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private final LocalDateTime orderDate; // complex attribute
    private String status;
    private double totalAmount; // derived attribute
    private List<Product> items; // Order consists of (1..*) Products

    public Order(LocalDateTime orderDate, String status) {
        if (orderDate == null) throw new IllegalArgumentException("Order date canot be null");
        if (orderDate.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Order date cannot be in the future");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status cannot be empty");

        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = 0;
        this.items = new ArrayList<>();
    }


    public String getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<Product> getItems() {
        return new ArrayList<>(items);
    }

    public void calculateTotal() {
        double sum = 0;
        for (Product p : items) {
            sum += p.getPrice();
        }
        totalAmount = sum;
    }

    public void changeOrderStatus(String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Invalid status");
        }
        status = newStatus;
    }

    public void checkPendingOrders() {
        if (status.equalsIgnoreCase("pending")) {
            System.out.println("Order is still pending...");
        }
    }

    @Override
    public String toString() {
        return "Order{" + "date=" + orderDate + ", status='" + status + '\'' + ", total=" + totalAmount + ", items=" + items.size() + '}';
    }
}