package pl.edu.pjwstk.byt;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "ShoppingCart_extent.ser";
    private static List<ShoppingCart> extent = new ArrayList<>();

    public static final int MAX_CART_ITEMS = 50; // class attribute

    private final Map<String, CartItem> cartItems = new HashMap<>(); // qualified association
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public ShoppingCart() {
        var now = LocalDateTime.now();
        createdDate = now;
        lastUpdated = now;
        extent.add(this);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Map<String, CartItem> getCartItems() {
        return Collections.unmodifiableMap(cartItems);
    }

    public void clearCart() {
        this.cartItems.clear();
        lastUpdated = LocalDateTime.now();
    }

    public boolean updateCart(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        var cartItemsCount = cartItems
            .values()
            .stream()
            .mapToInt(CartItem::getQuantity)
            .sum();

        if (quantity + cartItemsCount > MAX_CART_ITEMS) {
            return false;
        }


        var cartItem = new CartItem(quantity, product, this);
        cartItems.put(product.getId(), cartItem);
        lastUpdated = LocalDateTime.now();
        return true;
    }

    public void remove(String productId) {
        if (!cartItems.containsKey(productId)) {
            throw new IllegalArgumentException("Product is not in the cart");
        }

        var cartItem = cartItems.get(productId);
        cartItem.setCart(null);
        cartItems.remove(productId);
        lastUpdated = LocalDateTime.now();
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
