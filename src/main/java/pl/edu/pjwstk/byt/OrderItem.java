package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "OrderItem_extent.ser";
    private static List<OrderItem> extent = new ArrayList<>();

    private Order order; // Component of Order (Whole)
    private Product product;
    private int quantity;

    public OrderItem(Order order, Product product, int quantity) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        this.order = order;
        this.product = product;
        this.quantity = quantity;

        this.order.addOrderItemInternal(this);

        extent.add(this);
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity;
        order.calculateTotal(); // Update whole
    }

    // Lifecycle method for Composition
    public void dispose() {
        extent.remove(this);
        if (order != null) {
            order.removeOrderItemInternal(this);
            order = null;
        }
    }

    // Internal use for Order.delete()
    protected void disposeWithoutRemovingFromOrder() {
        extent.remove(this);
        this.order = null;
    }

    // Extent persistence
    public static List<OrderItem> getExtent() {
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
            extent = (List<OrderItem>) ois.readObject();
        }
    }

    @Override
    public String toString() {
        return String.format("%s x %d", product.getName(), quantity);
    }
}
