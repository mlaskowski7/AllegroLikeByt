package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RegularUserTest {

    @Test
    void shouldCreateRegularUserWithRequiredFields() {
        RegularUser user = new RegularUser("john_doe", "john@example.com");

        assertNotNull(user);
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertTrue(user.isActive());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    void shouldManageShippingAddresses() {
        RegularUser user = new RegularUser("jane_doe", "jane@example.com");
        List<String> addresses = List.of("123 Main St", "456 Side St");

        user.setShippingAdresses(addresses);

        assertEquals(2, user.getShippingAdresses().size());
        assertTrue(user.getShippingAdresses().contains("123 Main St"));
    }

    @Test
    void shouldExecuteBusinessMethods() {
        RegularUser user = new RegularUser("bob_builder", "bob@example.com");

        assertDoesNotThrow(() -> {
            user.addFunds(100.0);
            user.purchasePlan("Premium");
            user.reviewProduct(null, "Good product"); // Product can be null for stub
        });
    }

    @Test
    void shouldManageShoppingCart() {
        RegularUser user = new RegularUser("shopper", "shop@example.com");
        ShoppingCart cart = new ShoppingCart();

        user.setShoppingCart(cart);
        assertEquals(cart, user.getShoppingCart());
    }

    @Test
    void shouldInheritUserProperties() {
        RegularUser user = new RegularUser("inherit_test", "inherit@example.com");
        user.setPassword("securePass");

        assertEquals("securePass", user.getPassword());
    }
}
