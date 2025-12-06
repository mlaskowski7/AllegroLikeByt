package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "CartItem_extent.ser";
    private static List<CartItem> extent = new ArrayList<>();

    private final Product product;

    private ShoppingCart cart; // reverse connection

    private int quantity;

    public CartItem(int quantity, Product product, ShoppingCart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart item must be instantiated with a shopping cart");
        }
        if (product == null) {
            throw new IllegalArgumentException("Cart item must have a product");
        }

        this.cart = cart;
        this.quantity = quantity;
        this.product = product;
        extent.add(this);
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public static List<CartItem> getExtent() {
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
            extent = (List<CartItem>) ois.readObject();
        }
    }
}
