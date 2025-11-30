package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "Product_extent.ser";
    private static List<Product> extent = new ArrayList<>();

    private String name; // basic attribute
    private String description;
    private double price;
    private int stockQuantity;
    private List<String> images; // multi value attribute [1..*]
    private List<Integer> rating; // list of ratings
    private double avgRating; // /avgRating (derived)

    public Product(String name, String description, double price, int stockQuantity, List<String> images) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description cannot be empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity cannot be negative");
        if (images == null || images.isEmpty()) throw new IllegalArgumentException("At least one image required");
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.images = new ArrayList<>(images);
        this.rating = new ArrayList<>();
        this.avgRating = 0;
        extent.add(this);
    }

    public void updateStock(int change) {
        if (stockQuantity + change < 0)
            throw new IllegalArgumentException("Not enough stock");
        stockQuantity += change;
    }

    public void calculateAverageRating() {
        if (rating.isEmpty()) {
            avgRating = 0;
            return;
        }
        double sum = 0;
        for (int r : rating) sum += r;
        avgRating = sum / rating.size();
    }

    public void addReview(int stars) {
        if (stars < 1 || stars > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        rating.add(stars);
        calculateAverageRating();
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getAvgRating() { return avgRating; }


    public static List<Product> getExtent() {
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
            extent = (List<Product>) ois.readObject();
        }
    }

    @Override
    public String toString() {
        return name + " | Price: " + price + " | Avg Rating: " + avgRating;
    }
}
