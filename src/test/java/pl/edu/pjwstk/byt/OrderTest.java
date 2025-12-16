package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private User user;
    private Product product;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        clearExtent(User.class);
        clearExtent(Product.class);
        user = new User("Test", "test@test.com");
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
        var order = new Order(user, product, 1);
        assertNotNull(order);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, product, 1));
    }

    @Test
    void shouldThrowExceptionWhenInitialProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(user, null, 1));
    }

    @Test
    void shouldSetStatusCorrectlyOnCreation() {
        // Status is pending by default in new constructor
        var order = new Order(user, product, 1);
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getStatus());
    }
}
