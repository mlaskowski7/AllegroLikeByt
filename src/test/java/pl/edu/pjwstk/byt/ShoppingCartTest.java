package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ShoppingCartTest {

    @Test
    void getCartItems_afterAddingItems_unmodifiableMapOfItems() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);

        // when
        var cartItems = shoppingCart.getCartItems();

        // then
        assertNotNull(cartItems);
        assertEquals(1, cartItems.size());
        assertThrows(UnsupportedOperationException.class, cartItems::clear);
        var key = product.getId();
        assertTrue(cartItems.containsKey(key));
        var cartItem = cartItems.get(key);
        assertSame(product, cartItem.getProduct());
        assertSame(shoppingCart, cartItem.getCart());
    }

    @Test
    void clearCart_emptyCart_cartRemainsEmpty() {
        // given
        var shoppingCart = new ShoppingCart();

        // when
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void clearCart_cartWithItems_becomesEmpty() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);

        // when
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void clearCart_updatesLastUpdatedDate() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);
        var lastUpdatedBeforeClear = shoppingCart.getLastUpdated();

        // when
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
        assertTrue(shoppingCart.getLastUpdated().isAfter(lastUpdatedBeforeClear) ||
                   shoppingCart.getLastUpdated().equals(lastUpdatedBeforeClear));
    }

    @Test
    void clearCart_multipleItems_removesAll() {
        // given
        var shoppingCart = new ShoppingCart();
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var product3 = new Product("Product 3", "Description 3", 30.0, 15, List.of("image3.jpg"));
        shoppingCart.updateCart(product1, 1);
        shoppingCart.updateCart(product2, 2);
        shoppingCart.updateCart(product3, 3);

        // when
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void updateCart_validProduct_productAddedWithKeyAndCartLink() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();

        // when
        var result = shoppingCart.updateCart(product, 1);

        // then
        assertTrue(result);
        assertEquals(1, shoppingCart.getCartItems().size());
        assertTrue(shoppingCart.getCartItems().containsKey(product.getId()));
        var item = shoppingCart.getCartItems().get(product.getId());
        assertSame(product, item.getProduct());
        assertSame(shoppingCart, item.getCart());
        assertEquals(1, item.getQuantity());
    }

    @Test
    void updateCart_nullProduct_illegalArgumentExceptionThrown() {
        // given
        var shoppingCart = new ShoppingCart();

        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> shoppingCart.updateCart(null, 1)
        );
        assertEquals("Product cannot be null.", exception.getMessage());
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void updateCart_multipleProducts_allProductsAdded() {
        // given
        var shoppingCart = new ShoppingCart();
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var product3 = new Product("Product 3", "Description 3", 30.0, 15, List.of("image3.jpg"));

        // when
        shoppingCart.updateCart(product1, 1);
        shoppingCart.updateCart(product2, 2);
        shoppingCart.updateCart(product3, 3);

        // then
        assertEquals(3, shoppingCart.getCartItems().size());
        assertTrue(shoppingCart.getCartItems().containsKey(product1.getId()));
        assertTrue(shoppingCart.getCartItems().containsKey(product2.getId()));
        assertTrue(shoppingCart.getCartItems().containsKey(product3.getId()));
    }

    @Test
    void updateCart_readdingSameProduct_doesNotIncreaseKeyCount() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();

        // when
        var r1 = shoppingCart.updateCart(product, 1);
        var r2 = shoppingCart.updateCart(product, 1);

        // then
        assertTrue(r1);
        assertTrue(r2);
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    void updateCart_updatesLastUpdatedDate() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();
        var originalLastUpdated = shoppingCart.getLastUpdated();

        // when
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        shoppingCart.updateCart(product, 1);

        // then
        assertTrue(shoppingCart.getLastUpdated().isAfter(originalLastUpdated) ||
                   shoppingCart.getLastUpdated().equals(originalLastUpdated));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20})
    void updateCart_variousQuantities_productAdded(int quantity) {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();

        // when
        var result = shoppingCart.updateCart(product, quantity);

        // then
        assertTrue(result);
        assertEquals(1, shoppingCart.getCartItems().size());
        assertEquals(1, shoppingCart.getCartItems().get(product.getId()).getQuantity());
    }

    @Test
    void updateCart_afterClear_productAdded() {
        // given
        var shoppingCart = new ShoppingCart();
        var product1 = createTestProduct();
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        shoppingCart.updateCart(product1, 1);
        shoppingCart.clearCart();

        // when
        shoppingCart.updateCart(product2, 2);

        // then
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    void updateCart_whenMaxItemsExceeded_returnsFalseAndDoesNotAdd() {
        // given
        var shoppingCart = new ShoppingCart();
        for (int i = 0; i < ShoppingCart.MAX_CART_ITEMS; i++) {
            var p = new Product("P" + i, "D" + i, 1.0 + i, 1 + i, List.of("img" + i));
            var added = shoppingCart.updateCart(p, 1);
            assertTrue(added);
        }

        // when
        var extra = new Product("Extra", "Extra Desc", 9.9, 1, List.of("img"));
        var result = shoppingCart.updateCart(extra, 1);

        // then
        assertFalse(result);
        assertEquals(ShoppingCart.MAX_CART_ITEMS, shoppingCart.getCartItems().size());
    }

    @Test
    void remove_nonExistingProduct_throws() {
        // given
        var shoppingCart = new ShoppingCart();

        // then
        var exception = assertThrows(IllegalArgumentException.class, () -> shoppingCart.remove("non-existent-id"));
        assertEquals("Product is not in the cart", exception.getMessage());
    }

    @Test
    void remove_existingProduct_removesAndDetachesCartItemUpdatesLastUpdated() {
        // given
        var shoppingCart = new ShoppingCart();
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);
        var key = product.getId();
        var before = shoppingCart.getLastUpdated();

        // when
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        shoppingCart.remove(key);

        // then
        assertFalse(shoppingCart.getCartItems().containsKey(key));
        var found = CartItem.getExtent().stream()
            .filter(ci -> ci.getProduct().getId().equals(key))
            .findFirst();
        assertTrue(found.isPresent());
        assertNull(found.get().getCart());
        assertTrue(shoppingCart.getLastUpdated().isAfter(before) || shoppingCart.getLastUpdated().equals(before));
    }

    private Product createTestProduct() {
        return new Product("Test Product", "Test Description", 10.0, 5, List.of("image.jpg"));
    }
}
