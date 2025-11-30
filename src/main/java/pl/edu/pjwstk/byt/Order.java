package pl.edu.pjwstk.byt;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private final LocalDateTime orderDate; // complex attribute
    private OrderStatus status;
    private double totalAmount; // derived attribute
    private List<Product> items; // Order consists of (1..*) Products

    public Order(LocalDateTime orderDate, OrderStatus status) {
        if (orderDate == null) throw new IllegalArgumentException("Order date cannot be null");
        if (orderDate.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Order date cannot be in the future");
        if (status == null) throw new IllegalArgumentException("Order status cannot be null");

        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = 0;
        this.items = new ArrayList<>();
    }


    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        this.status = status;
    }


    public OrderStatus getStatus() {
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


    public void checkPendingOrders() {
        if (status == OrderStatus.PAYMENT_PENDING) {
            System.out.println("Order is still pending...");
        }
    }

    @Override
    public String toString() {
        return "Order{" + "date=" + orderDate + ", status='" + status + '\'' + ", total=" + totalAmount + ", items=" + items.size() + '}';
    }
}