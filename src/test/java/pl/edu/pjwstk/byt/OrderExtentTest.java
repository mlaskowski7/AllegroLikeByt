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

    private RegularUser user;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        try {
            clearExtent(Order.class);
            clearExtent(Product.class);
            clearExtent(OrderItem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new File(EXTENT_FILE).delete();
        new File("Product_extent.ser").delete();
        new File("OrderItem_extent.ser").delete();

        user = new RegularUser("TestUser", "test@example.com");
        product = new Product("P", "D", 10.0, 10, List.of("img"));
    }

    private void clearExtent(Class<?> clazz) throws Exception {
        Field field = clazz.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearExtent(Order.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        new File(EXTENT_FILE).delete();
        new File("Product_extent.ser").delete();
        new File("OrderItem_extent.ser").delete();
    }

    @Test
    void getExtent_afterCreatingOrders_returnsAllOrders() {
        var order1 = new Order(user, product, 1);
        var order2 = new Order(user, product, 2);

        List<Order> orders = Order.getExtent();
        assertEquals(2, orders.size());
    }

    @Test
    void shouldRemoveOrderFromExtentOnDelete() {
        Order o = new Order(user, product, 1);
        o.delete();

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
        var order = new Order(user, product, 1);
        var extent1 = Order.getExtent();
        int originalSize = extent1.size();

        extent1.clear();
        var extent2 = Order.getExtent();

        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(order));
    }

    @Test
    void saveExtent_afterCreatingOrders_savesToFile() throws IOException {
        var order1 = new Order(user, product, 1);
        var order2 = new Order(user, product, 2);

        Order.saveExtent();

        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllOrders() throws Exception {
        var order1 = new Order(user, product, 1);
        order1.changeOrderStatus(OrderStatus.PAYMENT_PENDING);

        var order2 = new Order(user, product, 2);
        order2.changeOrderStatus(OrderStatus.COMPLETE);

        Product.saveExtent();
        OrderItem.saveExtent();
        Order.saveExtent();

        clearExtent(Order.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        Product.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(2, loadedExtent.size());
        assertTrue(loadedExtent.stream().anyMatch(o -> o.getStatus() == OrderStatus.PAYMENT_PENDING));
        assertTrue(loadedExtent.stream().anyMatch(o -> o.getStatus() == OrderStatus.COMPLETE));
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        new File(EXTENT_FILE).delete();
        assertThrows(IOException.class, () -> Order.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesOrderAttributes() throws Exception {
        var order = new Order(user, product, 1);
        order.changeOrderStatus(OrderStatus.COMPLETE);

        var newProduct = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        order.addProduct(newProduct, 1);
        order.calculateTotal();

        Product.saveExtent();
        OrderItem.saveExtent();
        Order.saveExtent();

        clearExtent(Order.class);
        clearExtent(Product.class);
        clearExtent(OrderItem.class);

        Product.loadExtent();
        OrderItem.loadExtent();
        Order.loadExtent();

        var loadedExtent = Order.getExtent();

        assertEquals(1, loadedExtent.size());
        var loadedOrder = loadedExtent.get(0);
        assertEquals(OrderStatus.COMPLETE, loadedOrder.getStatus());
        assertEquals(2, loadedOrder.getItems().size());
    }
}
