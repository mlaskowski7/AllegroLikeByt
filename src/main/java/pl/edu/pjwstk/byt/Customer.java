package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "Customer_extent.ser";
    private static List<Customer> extent = new ArrayList<>();

    private String name;
    private String email;
    private List<Order> orders = new ArrayList<>();

    public Customer(String name, String email) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty");

        this.name = name;
        this.email = email;
        extent.add(this);
    }

    public void addOrder(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");
        if (!orders.contains(order)) {
            orders.add(order);
            // Reverse connection
            if (order.getCustomer() != this) {
                order.setCustomer(this);
            }
        }
    }

    public void removeOrder(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        // If the order is still linked to us, deleting it is the only way to remove it
        // because Order requires a Customer (multiplicity 1).
        if (order.getCustomer() == this) {
            order.delete();
        } else {
            // If already unlinked (e.g. called from Order.delete() or Order.setCustomer()),
            // just remove from implementation list.
            orders.remove(order);
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    // Persistence
    public static List<Customer> getExtent() {
        return new ArrayList<>(extent);
    }

    public static void saveExtent() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            oos.writeObject(extent);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Customer>) ois.readObject();
        }
    }
}
