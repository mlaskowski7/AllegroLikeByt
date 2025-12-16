package pl.edu.pjwstk.byt;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "Order_extent.ser";
    private static List<Order> extent = new ArrayList<>();

    private final LocalDateTime orderDate; // complex attribute
    private OrderStatus status;
    private double totalAmount; // derived attribute

    // Composition: Order (Whole) <-> OrderItem (Part)
    private List<OrderItem> items;

    // Basic Association: User (1) <-> Order (*)
    private User user;

    public Order(User user, Product initialProduct, int initialQuantity) {
        if (user == null)
            throw new IllegalArgumentException("Order must have a user");
        if (initialProduct == null)
            throw new IllegalArgumentException("Order must have at least one product");
        if (initialQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        this.status = OrderStatus.PAYMENT_PENDING;
        this.orderDate = LocalDateTime.now();
        this.totalAmount = 0;
        this.items = new ArrayList<>();

        // Basic Association: User (1)
        setUser(user);

        // Composition: Items (1..*)
        // We create the first item.
        addProduct(initialProduct, initialQuantity);

        extent.add(this);
    }

    // ------------------------------------------------------------------------
    // Basic Association: User
    // ------------------------------------------------------------------------

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Order must have a user (multiplicity 1)");
        }
        if (this.user == user) {
            return;
        }

        User oldUser = this.user;
        this.user = null; // Decouple

        if (oldUser != null) {
            oldUser.removeOrder(this);
        }

        this.user = user;
        // Use internal method to avoid infinite recursion
        this.user.addOrderInternal(this);
    }

    /**
     * Internal method to set the user without triggering the reverse
     * association.
     * Should only be called from User.addOrderInternal.
     */
    protected void setUserInternal(User user) {
        this.user = user;
    }
    // We remove 'removeUser' public method because multiplicity is 1.
    // However, for destruction (delete), we might need internal cleanup.

    // ------------------------------------------------------------------------
    // Composition: OrderItem
    // ------------------------------------------------------------------------

    public OrderItem addProduct(Product product, int quantity) {
        return new OrderItem(this, product, quantity);
    }

    protected void addOrderItemInternal(OrderItem item) {
        if (!items.contains(item)) {
            items.add(item);
            calculateTotal();
        }
    }

    protected void removeOrderItemInternal(OrderItem item) {
        // Enforce multiplicity 1..*
        if (items.size() <= 1 && items.contains(item)) {
            throw new IllegalStateException("Cannot remove the last item from the order");
        }
        items.remove(item);
        calculateTotal();
    }

    public void removeOrderItem(OrderItem item) {
        if (items.contains(item)) {
            if (items.size() <= 1) {
                throw new IllegalStateException(
                        "Cannot remove the last item from the order. Delete the order instead.");
            }
            item.dispose();
        }
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Optional: Legacy support or helper
    public void addItem(Product product) {
        addProduct(product, 1);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    public void changeOrderStatus(OrderStatus status) {
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

    public void calculateTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getProduct().getPrice() * item.getQuantity();
        }
        totalAmount = sum;
    }

    public void checkPendingOrders() {
        if (status == OrderStatus.PAYMENT_PENDING) {
            System.out.println("Order is still pending...");
        }
    }

    // Lifecycle: Delete Order -> Delete Parts (Composition)
    public void delete() {
        extent.remove(this);

        // Dispose all parts
        // Use copy to avoid ConcurrentModificationException
        List<OrderItem> copy = new ArrayList<>(items);

        // Clear internal list first so we don't have removal issues
        items.clear();

        for (OrderItem item : copy) {
            // Let's just remove from extent manually since we are the aggregate root.
            item.disposeWithoutRemovingFromOrder();
        }

        // Unlink user
        if (this.user != null) {
            User u = this.user;
            this.user = null;
            u.removeOrder(this);
        }
    }

    @Override
    public String toString() {
        return "Order{" + "date=" + orderDate + ", status='" + status + '\'' + ", total=" + totalAmount + ", items="
                + items.size() + '}';
    }

    public static List<Order> getExtent() {
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
            extent = (List<Order>) ois.readObject();
        }
    }
}