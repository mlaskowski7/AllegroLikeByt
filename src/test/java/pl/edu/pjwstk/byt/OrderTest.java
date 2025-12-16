package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Customer customer;
    private Product product;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        clearExtent(Customer.class);
        clearExtent(Product.class);
        customer = new Customer("Test", "test@test.com");
        product = new Product("P", "D", 10, 10, java.util.List.of("img"));
    }

    private void clearExtent(Class<?> clazz) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField("extent");
        field.setAccessible(true);
        ((java.util.List<?>) field.get(null)).clear();
    }

    // Complex attribute (orderDate) TESTS
    @Test
    void shouldCreateOrderWithValidDate() {
        var order = new Order(customer, product, 1);
        assertNotNull(order);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, product, 1));
    }

    @Test
    void shouldThrowExceptionWhenInitialProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(customer, null, 1));
    }

    @Test
    void shouldSetStatusCorrectlyOnCreation() {
        // Status is pending by default in new constructor
        var order = new Order(customer, product, 1);
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getStatus());
    }
}
