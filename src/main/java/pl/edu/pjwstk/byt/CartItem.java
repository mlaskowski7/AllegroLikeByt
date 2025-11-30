package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "CartItem_extent.ser";
    private static List<CartItem> extent = new ArrayList<>();

    private final Product product;

    private int quantity;

    public CartItem(int quantity, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cart item must have a product");
        }

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

    // Class extent methods
    public static List<CartItem> getExtent() {
        return new ArrayList<>(extent);
    }

    public static void clearExtent() {
        extent.clear();
    }

    // Class extent persistence methods
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
