package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderExtentTest {

    private static final String EXTENT_FILE = "Order_extent.ser";

    private User user;
    private Product product;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        // Clear all extents via reflection
        try {
            clearExtent(Order.class);
            clearExtent(User.class);
            clearExtent(Product.class);
            clearExtent(OrderItem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        customer = new Customer("Test", "test@example.com");
        product = new Product("P", "D", 10.0, 10, List.of("img"));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        clearExtent(Order.class);
        clearExtent(User.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        new File(EXTENT_FILE).delete();
        new File("User_extent.ser").delete();
        new File("Product_extent.ser").delete();
        new File("OrderItem_extent.ser").delete();
    }

    @Test
    void getExtent_afterCreatingOrders_returnsAllOrders() {
        // given
        var order1 = new Order(customer, product, 1);
        var order2 = new Order(customer, product, 2);


        List<Order> orders = Order.getExtent();
        assertEquals(2, orders.size());
    }

    @Test
    void shouldRemoveOrderFromExtentOnDelete() {
        Order o = new Order(user, product, 1);
        o.delete();

        // then
        assertEquals(2, extent.size());
        assertTrue(extent.contains(order1));
        assertTrue(extent.contains(order2));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        var extent = Order.getExtent();
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var order = new Order(customer, product, 1);
        var extent1 = Order.getExtent();
        int originalSize = extent1.size();

        extent1.clear();
        var extent2 = Order.getExtent();

        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(order));
    }

    @Test
    void saveExtent_afterCreatingOrders_savesToFile() throws IOException {
        // given
        var order1 = new Order(customer, product, 1);
        var order2 = new Order(customer, product, 2);

        Order.saveExtent();

        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllOrders() throws Exception {
        // given
        var order1 = new Order(customer, product, 1);
        order1.changeOrderStatus(OrderStatus.PAYMENT_PENDING);

        var order2 = new Order(customer, product, 2);
        order2.changeOrderStatus(OrderStatus.COMPLETE);

        Order.saveExtent();

        // Clear memory only, do NOT delete files (which setUp() does)
        clearExtent(Order.class);
        clearExtent(User.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        // Load
        Product.loadExtent();
        User.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(2, loadedExtent.size());
        // Since load order works by serialization, order1 might be first or second
        // depending on list order.
        // List order is insertion order.
        assertEquals(OrderStatus.PAYMENT_PENDING, loadedExtent.get(0).getStatus());
        assertEquals(OrderStatus.COMPLETE, loadedExtent.get(1).getStatus());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // Ensure file is gone
        new File(EXTENT_FILE).delete();
        assertThrows(IOException.class, () -> Order.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesOrderAttributes() throws Exception {
        // given
        var order = new Order(customer, product, 1);
        order.changeOrderStatus(OrderStatus.COMPLETE);

        var newProduct = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        order.addProduct(newProduct, 1);
        order.calculateTotal();

        Order.saveExtent();

        // Clear memory only
        clearExtent(Order.class);
        clearExtent(User.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        // Load
        Product.loadExtent();
        User.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(1, loadedExtent.size());
        var loadedOrder = loadedExtent.get(0);
        assertEquals(OrderStatus.COMPLETE, loadedOrder.getStatus());
        assertEquals(2, loadedOrder.getItems().size());
    }
}
