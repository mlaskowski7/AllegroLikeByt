package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Customer customer;
    private Product product;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Customer.clearExtent();
        Product.clearExtent();
        customer = new Customer("Test", "test@test.com");
        product = new Product("P", "D", 10, 10, java.util.List.of("img"));
    }

    // Complex attribute (orderDate) TESTS
    @Test
    void shouldCreateOrderWithValidDate() {
        var order = new Order(customer, product, 1);
        assertNotNull(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderDateIsNull() {
        // Since date is now internal in constructor (LocalDateTime.now()), we can't
        // pass null.
        // We should test valid creation logic instead or private check?
        // Actually, I removed the date parameter from constructor.
        // So this test is obsolete or should strict check other params.
        // Let's test null customer/product instead.
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
