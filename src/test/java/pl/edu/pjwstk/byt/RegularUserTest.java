package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegularUserTest {

    private RegularUser user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new RegularUser("testUser", "user@example.com");
        product = new Product(
                "Laptop",
                "High-end laptop",
                1500.0,
                10,
                Arrays.asList("img1.jpg")
        );
    }


    @Test
    void regularUserShouldBeInstanceOfUser() {
        assertTrue(user instanceof User);
    }

    @Test
    void userCountShouldIncrease() {
        int countBefore = User.usercount;
        RegularUser newUser = new RegularUser("anotherUser", "another@example.com");
        assertEquals(countBefore + 1, User.usercount);
    }

    @Test
    void shouldAddShippingAddress() {
        Adress adress = new Adress("123 Main St", "Warsaw", "Poland", "00-001");
        user.addShippingAdress(adress);

        List<Adress> addresses = user.getShippingAdresses();
        assertEquals(1, addresses.size());
        assertTrue(addresses.contains(adress));
    }

    @Test
    void shouldRemoveShippingAddress() {
        Adress adress = new Adress("123 Main St", "Warsaw", "Poland", "00-001");
        user.addShippingAdress(adress);
        user.removeShippingAdress(adress);

        List<Adress> addresses = user.getShippingAdresses();
        assertEquals(0, addresses.size());
    }

    @Test
    void shouldAddOrder() {
        Order order = new Order(
                user, product, 1); // assuming default constructor exists
        user.addOrder(order);

        List<Order> orders = user.getOrders();
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order));
    }

    @Test
    void shouldHaveEmptyShoppingCartInitially() {
        assertNotNull(user.getShoppingCart());
        assertEquals(0, user.getShoppingCart().getCartItems().size());
    }

    @Test
    void shouldAddProductToShoppingCart() {
        Product product = new Product(
                "Laptop",                  // name
                "High-end laptop",         // description
                1500.0,                    // price
                10,                        // stock quantity
                List.of("img1.jpg")        // images
        );

        boolean added = user.getShoppingCart().updateCart(product, 1);
        assertTrue(added);

        assertEquals(1, user.getShoppingCart().getCartItems().size());
        assertTrue(user.getShoppingCart().getCartItems().containsKey(product.getId()));
    }

    @Test
    void shouldClearShoppingCart() {
        Product product = new Product(
                "Laptop",
                "High-end laptop",
                1500.0,
                10,
                List.of("img1.jpg")
        );

        user.getShoppingCart().updateCart(product, 1);

        user.clearShoppingCart();
        assertEquals(0, user.getShoppingCart().getCartItems().size());
    }

}
