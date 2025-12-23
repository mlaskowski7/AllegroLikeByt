package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private RegularUser user;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        clearExtent(Order.class);
        clearExtent(Product.class);
        user = new RegularUser("TestUser", "test@test.com");
        product = new Product("P", "D", 10, 10, List.of("img"));
    }

    private void clearExtent(Class<?> clazz) throws Exception {
        var field = clazz.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();
    }

    // Complex attribute (orderDate) TESTS
    @Test
    void shouldCreateOrderWithValidDate() {
        var order = new Order(user, product, 1);
        assertNotNull(order);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, product, 1));
    }

    @Test
    void shouldThrowExceptionWhenInitialProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(user, null, 1));
    }

    @Test
    void shouldSetStatusCorrectlyOnCreation() {
        var order = new Order(user, product, 1);
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getStatus());
    }

    @Test
    void shouldLinkOrderToUser() {
        var order = new Order(user, product, 1);
        assertTrue(user.getOrders().contains(order));
    }
}
