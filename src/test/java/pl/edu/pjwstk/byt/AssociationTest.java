package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssociationTest {

    private void clearExtents() throws Exception {
        clearExtent(Order.class, "extent");
        clearExtent(User.class, "extent");
        clearExtent(User.class, "usercount");
        clearExtent(OrderItem.class, "extent");
        clearExtent(CartItem.class, "extent");
        clearExtent(ShoppingCart.class, "extent");
    }

    private void clearExtent(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(null);
        if (value instanceof List<?> list) {
            list.clear();
        } else if (field.getType() == int.class) {
            field.set(null, 0);
        }
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
    // 1. Basic Association: RegularUser <-> Order
    // ------------------------------------------------------------------------
    @Test
    void shouldLinkUserAndOrderBidirectionallyOnCreation() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));

        Order order1 = new Order(alice, product, 1);

        assertEquals(alice, order1.getCustomer(), "Order should know its customer");
        assertTrue(alice.getOrders().contains(order1), "User should have the order");
    }

    @Test
    void shouldUnlinkUserWhenOrderIsDeleted() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));
        Order order1 = new Order(alice, product, 1);

        order1.delete();

        assertFalse(alice.getOrders().contains(order1), "User should not have the order");
    }

    @Test
    void shouldRemoveOrderFromUserWhenExplicitlyRemoved() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));
        Order order1 = new Order(alice, product, 1);

        alice.removeOrder(order1);

        assertFalse(alice.getOrders().contains(order1));
        assertFalse(Order.getExtent().contains(order1), "Order should be deleted from extent");
    }

    @Test
    void shouldSwitchUserCorrectly() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        RegularUser bob = new RegularUser("Bob", "bob@example.com");
        Product product = new Product("P", "D", 10, 10, List.of("img"));

        Order order1 = new Order(alice, product, 1);

        assertTrue(alice.getOrders().contains(order1));

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
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, List.of("img"));

        Order order = new Order(alice, product, 2);

        assertEquals(1, order.getItems().size());
        assertEquals(product, order.getItems().get(0).getProduct());
        assertEquals(50.0, order.getTotalAmount(), 0.01, "Total should be calculated");
    }

    @Test
    void shouldPreventRemovingLastItemFromOrder() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product = new Product("Comic", "Desc", 25.0, 10, List.of("img"));
        Order order = new Order(alice, product, 1);

        OrderItem item = order.getItems().get(0);

        assertThrows(IllegalStateException.class, () -> order.removeOrderItem(item));
    }

    @Test
    void shouldCascadeDeletePartsWhenWholeIsDeleted() {
        RegularUser alice = new RegularUser("Alice", "alice@example.com");
        Product product1 = new Product("P1", "D", 10.0, 10, List.of("i"));
        Product product2 = new Product("P2", "D", 20.0, 10, List.of("i"));

        Order order = new Order(alice, product1, 1);
        OrderItem item1 = order.getItems().get(0);

        OrderItem item2 = order.addProduct(product2, 2);

        assertTrue(OrderItem.getExtent().contains(item1));
        assertTrue(OrderItem.getExtent().contains(item2));

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
        Product product = new Product("Figurine", "Desc", 50.0, 5, List.of("img"));

        cart.updateCart(product, 3);

        assertTrue(cart.getCartItems().containsKey(product.getId()));
        CartItem item = cart.getCartItems().get(product.getId());

        assertEquals(3, item.getQuantity());
        assertEquals(product, item.getProduct());
        assertEquals(cart, item.getCart());

        cart.remove(product.getId());
        assertFalse(cart.getCartItems().containsKey(product.getId()));
        assertNull(item.getCart(), "Link object should know it's detached from cart");
    }

    @Test
    void shouldHandleDuplicateAssociationWithAttribute() {
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("Figurine", "Desc", 50.0, 5, List.of("img"));

        cart.updateCart(product, 2);
        cart.updateCart(product, 2);

        assertEquals(1, cart.getCartItems().size());
        assertEquals(2, cart.getCartItems().get(product.getId()).getQuantity());
    }
}
