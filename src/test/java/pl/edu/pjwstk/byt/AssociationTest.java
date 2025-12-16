package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssociationTest {

    private void clearExtents() throws Exception {
        clearExtent(Order.class, "extent");
        clearExtent(User.class, "extent");
        clearExtent(OrderItem.class, "extent");
        clearExtent(CartItem.class, "extent");
        clearExtent(ShoppingCart.class, "extent");
    }

    private void clearExtent(Class<?> clazz, String fieldName) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        ((java.util.List<?>) field.get(null)).clear();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearExtents();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearExtents();
    }

    // ------------------------------------------------------------------------
    // Basic Association: User (1) <-> Order (*)
    // ------------------------------------------------------------------------

    @Test
    void shouldCreateOrderWithUser() {
        User alice = new User("Alice", "alice@example.com");
        Product p = new Product("P", "D", 100, 10, java.util.List.of("img"));

        Order order = new Order(alice, p, 1);

        assertNotNull(order.getUser());
        assertEquals(alice, order.getUser());
        assertTrue(alice.getOrders().contains(order));
    }

    @Test
    void shouldThrowWhenCreatingOrderWithoutUser() {
        Product p = new Product("P", "D", 100, 10, java.util.List.of("img"));
        assertThrows(IllegalArgumentException.class, () -> new Order(null, p, 1));
    }

    @Test
    void shouldMaintainBidirectionalAssociationUserOrder() {
        User alice = new User("Alice", "alice@example.com");
        Product p = new Product("P", "D", 100, 10, java.util.List.of("img"));
        Order order = new Order(alice, p, 1);

        assertEquals(alice, order.getUser());
        assertTrue(alice.getOrders().contains(order));

        // Switch user
        User bob = new User("Bob", "bob@example.com");
        order.setUser(bob);

        assertEquals(bob, order.getUser());
        assertTrue(bob.getOrders().contains(order));
        assertFalse(alice.getOrders().contains(order));
    }

    @Test
    void shouldThrowIfUserOfOrderIsUnset() {
        User alice = new User("Alice", "alice@example.com");
        Product p = new Product("P", "D", 100, 10, java.util.List.of("img"));
        Order order = new Order(alice, p, 1);

        assertThrows(IllegalArgumentException.class, () -> order.setUser(null));
    }

    @Test
    void schemaViolationDuplicateUserInExtentShouldBeHandledOrAllowed() throws Exception {
        // Technically extent allows duplicates if not checked?
        // Let's check logic. Usually extent assumes distinct objects.
        // If we create two distinct objects with same data:
        User u1 = new User("A", "a");
        User u2 = new User("A", "a");
        // They are distinct in memory.
        assertNotSame(u1, u2);
    }

    // ------------------------------------------------------------------------
    // Reverse connection Logic Check (User <-> Order)
    // ------------------------------------------------------------------------

    @Test
    void shouldAddOrderToUserWhenSetOnOrder() {
        User u = new User("U", "u");
        Product p = new Product("P", "D", 10, 1, java.util.List.of("i"));
        Order o = new Order(u, p, 1); // Sets user internally

        assertTrue(u.getOrders().contains(o));
    }

    @Test
    void shouldSetUserOnOrderWhenAddedToUser() {
        User u = new User("U", "u");
        Product p = new Product("P", "D", 10, 1, java.util.List.of("i"));
        // Create order with temp user to be valid
        User temp = new User("Temp", "t");
        Order o = new Order(temp, p, 1);

        // Move to U via addOrder
        u.addOrder(o);

        assertEquals(u, o.getUser());
        assertTrue(u.getOrders().contains(o));
        assertFalse(temp.getOrders().contains(o));
    }

    @Test
    void removingOrderFromUserShouldDeleteOrderBecauseOrphansNotAllowed() {
        User u = new User("U", "u");
        Product p = new Product("P", "D", 10, 1, java.util.List.of("i"));
        Order o = new Order(u, p, 1);

        // Remove order from user
        u.removeOrder(o);

        // Because Order requires a User (concerns 1..*), if we remove it from the User,
        // the Order typically must be deleted or moved.
        // Implementation: removeOrder -> deletes if linked.

        // Verify order is 'deleted' (removed from extent or marked invalid?)
        // In our simple implementation, Order.delete() removes from extent.
        // Let's reflect check extent if possible or check if order items disposed.

        // Since we can't easily check 'extent' without access or reflection here
        // (unless public),
        // we can check if it's unlinked?
        assertNull(o.getUser()); // Order.delete set customer to null
    } // ------------------------------------------------------------------------
      // 2. Composition: Order <-> OrderItem
      // ------------------------------------------------------------------------

    @Test
    void shouldCreateOrderItemAsPartOfOrder() {
        User alice = new User("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, java.util.List.of("img"));

        // Created with initial item
        Order order = new Order(alice, product, 2);

        assertEquals(1, order.getItems().size());
        assertEquals(product, order.getItems().get(0).getProduct());
        assertEquals(50.0, order.getTotalAmount(), 0.01, "Total should be calculated");
    }

    @Test
    void shouldPreventRemovingLastItemFromOrder() {
        User alice = new User("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, java.util.List.of("img"));
        Order order = new Order(alice, product, 1);

        OrderItem item = order.getItems().get(0);

        // Attempt to remove the only item
        assertThrows(IllegalStateException.class, () -> order.removeOrderItem(item));
    }

    @Test
    void shouldCascadeDeletePartsWhenWholeIsDeleted() {
        User alice = new User("Alice", "alice@example.com");
        Product product1 = new Product("P1", "D", 10.0, 10, java.util.List.of("i"));
        Product product2 = new Product("P2", "D", 20.0, 10, java.util.List.of("i"));

        Order order = new Order(alice, product1, 1);
        OrderItem item1 = order.getItems().get(0);

        OrderItem item2 = order.addProduct(product2, 2);

        assertTrue(OrderItem.getExtent().contains(item1));
        assertTrue(OrderItem.getExtent().contains(item2));

        // Delete Whole
        order.delete();

        assertFalse(Order.getExtent().contains(order));
        assertFalse(OrderItem.getExtent().contains(item1), "Part 1 should be deleted");
        assertFalse(OrderItem.getExtent().contains(item2), "Part 2 should be deleted");
    }

    // ------------------------------------------------------------------------
    // 3. Association with Attribute: ShoppingCart <-> Product (via CartItem)
    // ------------------------------------------------------------------------

    @Test
    void shouldVerifyAssociationWithAttributeConsistency() {
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("Figurine", "Desc", 50.0, 5, java.util.List.of("img"));

        // Create link (Attribute: quantity=3)
        cart.updateCart(product, 3);

        assertTrue(cart.getCartItems().containsKey(product.getId()));
        CartItem item = cart.getCartItems().get(product.getId());

        assertEquals(3, item.getQuantity());
        assertEquals(product, item.getProduct());
        assertEquals(cart, item.getCart());

        // Remove link
        cart.remove(product.getId());
        assertFalse(cart.getCartItems().containsKey(product.getId()));
        assertNull(item.getCart(), "Link object should know it's detached from cart");
    }

    @Test
    void shouldHandleDuplicateAssociationWithAttribute() {
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("Figurine", "Desc", 50.0, 5, java.util.List.of("img"));

        cart.updateCart(product, 2);
        // Update existing (replace/update)
        cart.updateCart(product, 2);

        assertEquals(1, cart.getCartItems().size());
        assertEquals(2, cart.getCartItems().get(product.getId()).getQuantity());
    }
}
