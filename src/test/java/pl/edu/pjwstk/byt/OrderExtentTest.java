package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderExtentTest {

    private static final String EXTENT_FILE = "Order_extent.ser";

    private void clearExtent() throws Exception {
        Field field = Order.class.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();
    }

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
        customer = new Customer("Test", "test@example.com");
        product = new Product("P", "D", 10.0, 10, List.of("img"));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        clearExtent();
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getExtent_afterCreatingOrders_returnsAllOrders() {
        // given
        var order1 = new Order(customer, product, 1);
        var order2 = new Order(customer, product, 2);


        // when
        var extent = Order.getExtent();

        // then
        assertEquals(2, extent.size());
        assertTrue(extent.contains(order1));
        assertTrue(extent.contains(order2));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        // when
        var extent = Order.getExtent();

        // then
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var order = new Order(customer, product, 1);
        var extent1 = Order.getExtent();
        int originalSize = extent1.size();

        // when
        extent1.clear(); // This should not affect the original extent
        var extent2 = Order.getExtent();

        // then
        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(order));
    }

    @Test
    void saveExtent_afterCreatingOrders_savesToFile() throws IOException {
        // given
        var order1 = new Order(customer, product, 1);
        var order2 = new Order(customer, product, 2);

        // when
        Order.saveExtent();

        // then
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

        clearExtent();

        // when
        Order.loadExtent();
        var loadedExtent = Order.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        // Since load order works by serialization, order1 might be first or second
        // depending on list order.
        // List order is insertion order.
        assertEquals(OrderStatus.PAYMENT_PENDING, loadedExtent.get(0).getStatus());
        assertEquals(OrderStatus.COMPLETE, loadedExtent.get(1).getStatus());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // then
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

        clearExtent();

        // when
        Order.loadExtent();
        var loadedExtent = Order.getExtent();

        // then
        assertEquals(1, loadedExtent.size());
        var loadedOrder = loadedExtent.get(0);
        assertEquals(OrderStatus.COMPLETE, loadedOrder.getStatus());
        assertEquals(2, loadedOrder.getItems().size());
    }
}
