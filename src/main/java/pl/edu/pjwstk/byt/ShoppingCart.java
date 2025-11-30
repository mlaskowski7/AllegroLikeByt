package pl.edu.pjwstk.byt;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "ShoppingCart_extent.ser";
    private static List<ShoppingCart> extent = new ArrayList<>();

    private static final int MAX_CART_ITEMS = 50;

    private final Set<CartItem> cartItems = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public ShoppingCart(LocalDateTime createdDate) {
        setCreatedDate(createdDate);
        setLastUpdated(createdDate);
        extent.add(this);
    }

    public static int getMaxCartItems() {
        return MAX_CART_ITEMS;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    private void setCreatedDate(LocalDateTime createdDate) {
        if (createdDate == null) {
            throw new IllegalArgumentException("Creation date cannot be null.");
        }
        if (createdDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Creation date cannot be in the future.");
        }
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        if (lastUpdated == null) {
            throw new IllegalArgumentException("Last updated date cannot be null.");
        }
        if (createdDate != null && lastUpdated.isBefore(createdDate)) {
            throw new IllegalArgumentException("Last updated date cannot be before creation date.");
        }
        this.lastUpdated = lastUpdated;
    }

    public Set<CartItem> getCartItems() {
        return Collections.unmodifiableSet(cartItems);
    }

    public void clearCart() {
        this.cartItems.clear();
        setLastUpdated(LocalDateTime.now());
    }

    public void updateCart(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        var cartItem = new CartItem(1, product);
        cartItems.add(cartItem);
        setLastUpdated(LocalDateTime.now());
    }

    public static List<ShoppingCart> getExtent() {
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
            extent = (List<ShoppingCart>) ois.readObject();
        }
    }
}
