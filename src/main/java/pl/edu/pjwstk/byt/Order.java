package pl.edu.pjwstk.byt;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // Basic Association: Customer (1) <-> Order (*)
    private Customer customer;

    public Order(Customer customer, Product initialProduct, int initialQuantity) {
        if (customer == null)
            throw new IllegalArgumentException("Order must have a customer");
        if (initialProduct == null)
            throw new IllegalArgumentException("Order must have at least one product");
        if (initialQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        this.status = OrderStatus.PAYMENT_PENDING;
        this.orderDate = LocalDateTime.now(); // Default or passed? Assuming current for new order
        this.totalAmount = 0;
        this.items = new ArrayList<>();

        // Basic Association: Customer (1)
        setCustomer(customer);

        // Composition: Items (1..*)
        // We create the first item.
        addProduct(initialProduct, initialQuantity);

        extent.add(this);
    }

    // ------------------------------------------------------------------------
    // Basic Association: Customer
    // ------------------------------------------------------------------------

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Order must have a customer (multiplicity 1)");
        }
        if (this.customer == customer) {
            return;
        }

        Customer oldCustomer = this.customer;
        this.customer = null; // Decouple

        if (oldCustomer != null) {
            oldCustomer.removeOrder(this);
        }

        this.customer = customer;
        // Use internal method to avoid infinite recursion
        this.customer.addOrderInternal(this);
    }

    /**
     * Internal method to set the customer without triggering the reverse association.
     * Should only be called from Customer.addOrderInternal.
     */
    protected void setCustomerInternal(Customer customer) {
        this.customer = customer;
    }
    // We remove 'removeCustomer' public method because multiplicity is 1.
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
            // If we are destroying the order, we allow this?
            // Usually constraint applies to "live" object.
            // But if we delete the order, we remove all items.
            // We need a flag or check if we are in delete process?
            // Or simply: Order cannot exist with 0 items.
            // Exception: unless the Order itself is being deleted.
            // For now, simpler check:
            throw new IllegalStateException("Cannot remove the last item from the order");
        }
        items.remove(item);
        calculateTotal();
    }

    // Special internal method for deletion
    private void removeOrderItemInternalForDelete(OrderItem item) {
        items.remove(item);
        // No total recalc needed really
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

        // Unlink customer
        if (this.customer != null) {
            Customer c = this.customer;
            this.customer = null;
            c.removeOrder(this);
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