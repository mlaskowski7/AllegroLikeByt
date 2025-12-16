package pl.edu.pjwstk.byt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Example class demonstrating the implementation of class extent and extent
 * persistence.
 * This class showcases how to use extent to store all instances of a class
 * and how to persist them to disk and load them back.
 */
public class ExtentPersistenceExample {

    public static void main(String[] args) {
        System.out.println("=== Class Extent and Persistence Example ===\n");

        // Example 1: Product class extent and persistence
        demonstrateProductExtentAndPersistence();

        // Example 2: Category class extent and persistence
        demonstrateCategoryExtentAndPersistence();

        // Example 3: Order class extent and persistence
        demonstrateOrderExtentAndPersistence();

        System.out.println("\n=== Example completed successfully ===");
    }

    private static void demonstrateProductExtentAndPersistence() {
        System.out.println("--- Product Extent and Persistence Example ---");

        // Create some products
        System.out.println("Creating products...");
        var product1 = new Product("Laptop", "High-performance laptop", 2999.99, 10,
                List.of("laptop1.jpg", "laptop2.jpg"));
        var product2 = new Product("Mouse", "Wireless mouse", 49.99, 50, List.of("mouse.jpg"));
        var product3 = new Product("Keyboard", "Mechanical keyboard", 129.99, 30, List.of("keyboard.jpg"));

        // Add reviews to products
        product1.addReview(5);
        product1.addReview(4);
        product2.addReview(5);
        product3.addReview(4);
        product3.addReview(5);

        // Display extent
        List<Product> extent = Product.getExtent();
        System.out.println("Current Product extent size: " + extent.size());
        System.out.println("Products in extent:");
        extent.forEach(p -> System.out
                .println("  - " + p.getName() + " (Price: " + p.getPrice() + ", Rating: " + p.getAvgRating() + ")"));

        // Save extent to file
        try {
            System.out.println("\nSaving Product extent to file...");
            Product.saveExtent();
            System.out.println("Product extent saved successfully!");

            // Load extent from file
            System.out.println("\nLoading Product extent from file...");
            Product.loadExtent();
            List<Product> loadedExtent = Product.getExtent();
            System.out.println("Product extent size after loading: " + loadedExtent.size());
            System.out.println("Loaded products:");
            loadedExtent.forEach(p -> System.out.println(
                    "  - " + p.getName() + " (Price: " + p.getPrice() + ", Rating: " + p.getAvgRating() + ")"));
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during Product persistence: " + e.getMessage());
        }

        System.out.println();
    }

    private static void demonstrateCategoryExtentAndPersistence() {
        System.out.println("--- Category Extent and Persistence Example ---");

        // Create category hierarchy
        System.out.println("Creating categories...");
        var electronics = new Category("Electronics", "Electronic devices and accessories", null);
        var computers = new Category("Computers", "Computer hardware and software", electronics);
        var phones = new Category("Phones", "Mobile phones and accessories", electronics);
        var clothing = new Category("Clothing", "Clothing and fashion items", null);

        // Verify hierarchy
        System.out.println("Category hierarchy created:");
        System.out.println(
                "  - " + electronics.getName() + " has " + electronics.getSubCategories().size() + " subcategories");
        System.out.println("  - " + computers.getName() + " parent: " + computers.getParentCategory().getName());
        System.out.println("  - " + phones.getName() + " parent: " + phones.getParentCategory().getName());
        System.out.println("  - " + clothing.getName() + " is a top-level category");

        // Display extent
        List<Category> extent = Category.getExtent();
        System.out.println("Current Category extent size: " + extent.size());
        System.out.println("Categories in extent:");
        extent.forEach(c -> {
            String parentName = c.getParentCategory() != null ? c.getParentCategory().getName() : "None";
            System.out.println("  - " + c.getName() + " (Parent: " + parentName + ")");
        });

        // Save extent to file
        try {
            System.out.println("\nSaving Category extent to file...");
            Category.saveExtent();
            System.out.println("Category extent saved successfully!");

            // Load extent from file
            System.out.println("\nLoading Category extent from file...");
            Category.loadExtent();
            List<Category> loadedExtent = Category.getExtent();
            System.out.println("Category extent size after loading: " + loadedExtent.size());
            System.out.println("Loaded categories:");
            loadedExtent.forEach(c -> {
                String parentName = c.getParentCategory() != null ? c.getParentCategory().getName() : "None";
                System.out.println("  - " + c.getName() + " (Parent: " + parentName + ")");
            });
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during Category persistence: " + e.getMessage());
        }

        System.out.println();
    }

    private static void demonstrateOrderExtentAndPersistence() {
        System.out.println("--- Order Extent and Persistence Example ---");

        // Create products for orders
        var product1 = new Product("Product A", "Description A", 100.0, 10, List.of("a.jpg"));
        var product2 = new Product("Product B", "Description B", 200.0, 20, List.of("b.jpg"));

        // Create orders
        System.out.println("Creating orders...");
        var customer = new Customer("Demo User", "demo@example.com");

        var order1 = new Order(customer, product1, 1);
        // product1 already added in constructor
        order1.addItem(product2);
        order1.calculateTotal();

        var order2 = new Order(customer, product1, 1);
        order2.changeOrderStatus(OrderStatus.COMPLETE);
        order2.calculateTotal();

        // Display extent
        List<Order> extent = Order.getExtent();
        System.out.println("Current Order extent size: " + extent.size());
        System.out.println("Orders in extent:");
        extent.forEach(o -> System.out.println("  - Order (Status: " + o.getStatus() + ", Total: " + o.getTotalAmount()
                + ", Items: " + o.getItems().size() + ")"));

        System.out.println("Original Order Total: " + order1.getTotalAmount()); // 2*25.0 + 1*100.0 = 150.0

        // 6. Save Extents
        try {
            System.out.println("Saving extents...");
            Product.saveExtent();
            Category.saveExtent();
            Customer.saveExtent();
            Order.saveExtent();
            // OrderItem.saveExtent(); // Usually handled? Explicitly for safety
            OrderItem.saveExtent();
            System.out.println("Extents saved successfully!");

            // 7. Clear memory (Simulate restart)
            System.out.println("Clearing memory...");
            // Not easily possible to clear static lists without methods or reflection here.
            // But we can reload into new process logic or just inspect files.
            // For this example, let's just Load back and see if counts match.

            // 8. Load Extents
            System.out.println("Loading extents...");
            Product.loadExtent();
            Category.loadExtent();
            Customer.loadExtent();
            OrderItem.loadExtent();
            Order.loadExtent();

            List<Order> loadedExtent = Order.getExtent();
            System.out.println("Loaded orders:");
            loadedExtent.forEach(o -> System.out.println("  - Order (Status: " + o.getStatus() + ", Total: "
                    + o.getTotalAmount() + ", Items: " + o.getItems().size() + ")"));
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during Order persistence: " + e.getMessage());
        }

        System.out.println();
    }
}
