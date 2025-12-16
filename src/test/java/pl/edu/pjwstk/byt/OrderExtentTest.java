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

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        // Clear all extents via reflection
        try {
            clearExtent(Order.class);
            clearExtent(Customer.class);
            clearExtent(Product.class);
            clearExtent(OrderItem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Delete persistence files
        new File(EXTENT_FILE).delete();
        new File("Customer_extent.ser").delete();
        new File("Product_extent.ser").delete();
        new File("OrderItem_extent.ser").delete();

        customer = new Customer("Test", "test@example.com");
        product = new Product("P", "D", 10.0, 10, List.of("img"));
    }

    private void clearExtent(Class<?> clazz) throws Exception {
        Field field = clazz.getDeclaredField("extent");
        field.setAccessible(true);
        ((java.util.List<?>) field.get(null)).clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        clearExtent(Order.class);
        clearExtent(Customer.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        new File(EXTENT_FILE).delete();
        new File("Customer_extent.ser").delete();
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
        Order o = new Order(customer, product, 1);
        o.delete();

        // then
        assertEquals(0, Order.getExtent().size());
        assertFalse(Order.getExtent().contains(o));
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

        Customer.saveExtent();
        Product.saveExtent();
        OrderItem.saveExtent();
        Order.saveExtent();

        // Clear memory only, do NOT delete files (which setUp() does)
        clearExtent(Order.class);
        clearExtent(Customer.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        // Load
        Product.loadExtent();
        Customer.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(2, loadedExtent.size());
        assertTrue(loadedExtent.stream().anyMatch(o -> o.getStatus() == OrderStatus.PAYMENT_PENDING));
        assertTrue(loadedExtent.stream().anyMatch(o -> o.getStatus() == OrderStatus.COMPLETE));
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

        Customer.saveExtent();
        Product.saveExtent();
        OrderItem.saveExtent();
        Order.saveExtent();

        // Clear memory only
        clearExtent(Order.class);
        clearExtent(Customer.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        // Load
        Product.loadExtent();
        Customer.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(1, loadedExtent.size());
        var loadedOrder = loadedExtent.get(0);
        assertEquals(OrderStatus.COMPLETE, loadedOrder.getStatus());
        assertEquals(2, loadedOrder.getItems().size());
    }
}
