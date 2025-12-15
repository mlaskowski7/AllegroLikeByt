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
        clearExtent(Customer.class, "extent");
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
    // 1. Basic Association: Customer <-> Order
    // ------------------------------------------------------------------------
    @Test
    void shouldLinkCustomerAndOrderBidirectionallyOnCreation() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));

        // Order requires Customer in constructor
        Order order1 = new Order(alice, product, 1);

        assertEquals(alice, order1.getCustomer(), "Order should know its customer");
        assertTrue(alice.getOrders().contains(order1), "Customer should have the order");
    }

    @Test
    void shouldUnlinkCustomerWhenOrderIsDeleted() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));
        Order order1 = new Order(alice, product, 1);

        // Delete order
        order1.delete();

        // Order is gone, so link is broken
        assertFalse(alice.getOrders().contains(order1), "Customer should not have the order");
    }

    @Test
    void shouldRemoveOrderFromCustomerWhenExplicitlyRemoved() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));
        Order order1 = new Order(alice, product, 1);

        // Removing order from customer -> Order cannot exist without Customer -> Order
        // must be deleted.
        // Or logic in Customer.removeOrder calls order.delete()

        alice.removeOrder(order1);

        assertFalse(alice.getOrders().contains(order1));
        assertFalse(Order.getExtent().contains(order1), "Order should be deleted from extent");
    }

    @Test
    void shouldSwitchCustomerCorrectly() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Customer bob = new Customer("Bob", "bob@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));

        Order order1 = new Order(alice, product, 1);

        assertTrue(alice.getOrders().contains(order1));

        // Switch via Order
        order1.setCustomer(bob);

        assertFalse(alice.getOrders().contains(order1), "Alice should lose order");
        assertTrue(bob.getOrders().contains(order1), "Bob should gain order");
        assertEquals(bob, order1.getCustomer(), "Order should point to Bob");
    }

    // ------------------------------------------------------------------------
    // 2. Composition: Order <-> OrderItem
    // ------------------------------------------------------------------------
    @Test
    void shouldCreateOrderItemAsPartOfOrder() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, java.util.List.of("img"));

        // Created with initial item
        Order order = new Order(alice, product, 2);

        assertEquals(1, order.getItems().size());
        assertEquals(product, order.getItems().get(0).getProduct());
        assertEquals(50.0, order.getTotalAmount(), 0.01, "Total should be calculated");
    }

    @Test
    void shouldPreventRemovingLastItemFromOrder() {
        Customer alice = new Customer("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, java.util.List.of("img"));
        Order order = new Order(alice, product, 1);

        OrderItem item = order.getItems().get(0);

        // Attempt to remove the only item
        assertThrows(IllegalStateException.class, () -> order.removeOrderItem(item));
    }

    @Test
    void shouldCascadeDeletePartsWhenWholeIsDeleted() {
        Customer alice = new Customer("Alice", "alice@example.com");
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
