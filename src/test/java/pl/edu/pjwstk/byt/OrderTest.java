package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    // Complex attribute (orderDate) TESTS
    @Test
    void shouldCreateOrderWithValidDate() {
        var order = new Order(LocalDateTime.now().minusDays(1), "pending");
        assertNotNull(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderDateIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(null, "pending")
        );
    }

    @Test
    void shouldThrowExceptionWhenOrderDateIsInFuture() {
        var futureDate = LocalDateTime.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () ->
                new Order(futureDate, "pending")
        );
    }

    @Test
    void shouldSetStatusCorrectlyOnCreation() {
        var now = LocalDateTime.now();
        var order = new Order(now, "completed");
        assertEquals("completed", order.getStatus());
    }
}
