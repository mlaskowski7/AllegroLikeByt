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

    @BeforeEach
    void setUp() throws Exception {
        clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
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
        var order1 = new Order(LocalDateTime.now().minusDays(1), OrderStatus.PAYMENT_PENDING);
        var order2 = new Order(LocalDateTime.now().minusDays(2), OrderStatus.COMPLETE);
        var order3 = new Order(LocalDateTime.now().minusDays(3), OrderStatus.SHIPPED);

        // when
        var extent = Order.getExtent();

        // then
        assertEquals(3, extent.size());
        assertTrue(extent.contains(order1));
        assertTrue(extent.contains(order2));
        assertTrue(extent.contains(order3));
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
        var order = new Order(LocalDateTime.now().minusDays(1), OrderStatus.PAYMENT_PENDING);
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
        var order1 = new Order(LocalDateTime.now().minusDays(1), OrderStatus.PAYMENT_PENDING);
        var order2 = new Order(LocalDateTime.now().minusDays(2), OrderStatus.COMPLETE);

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
        var order1 = new Order(LocalDateTime.now().minusDays(1), OrderStatus.PAYMENT_PENDING);
        var order2 = new Order(LocalDateTime.now().minusDays(2), OrderStatus.COMPLETE);
        Order.saveExtent();

        clearExtent();

        // when
        Order.loadExtent();
        var loadedExtent = Order.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        assertEquals("pending", loadedExtent.get(0).getStatus());
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
        var orderDate = LocalDateTime.now().minusDays(5);
        var order = new Order(orderDate, OrderStatus.PAYMENT_PENDING);
        order.changeOrderStatus(OrderStatus.COMPLETE);
        var product = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        order.addItem(product);
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
        assertEquals(1, loadedOrder.getItems().size());
    }
}

