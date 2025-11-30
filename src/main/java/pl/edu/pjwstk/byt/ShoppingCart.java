package pl.edu.pjwstk.byt;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ShoppingCart {

    public static final int MAX_CART_ITEMS = 50; // class attribute

    private final Set<CartItem> cartItems = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public ShoppingCart() {
        var now = LocalDateTime.now();
        createdDate = now;
        lastUpdated = now;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Set<CartItem> getCartItems() {
        return Collections.unmodifiableSet(cartItems);
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
            .stream()
            .mapToInt(ci -> ci.getQuantity())
            .sum();

        if (quantity + cartItemsCount > MAX_CART_ITEMS) {
            return false;
        }
        

        var cartItem = new CartItem(1, product);
        cartItems.add(cartItem);
        lastUpdated = LocalDateTime.now();
        return true;
    }
}
