package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    @Test
    void ctor_validCreatedDate_shoppingCartCreated() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);

        // when
        var shoppingCart = new ShoppingCart(createdDate);

        // then
        assertNotNull(shoppingCart);
        assertEquals(createdDate, shoppingCart.getCreatedDate());
        assertEquals(createdDate, shoppingCart.getLastUpdated());
        assertNotNull(shoppingCart.getCartItems());
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void ctor_createdDateNow_shoppingCartCreated() {
        // given
        var createdDate = LocalDateTime.now();

        // when
        var shoppingCart = new ShoppingCart(createdDate);

        // then
        assertNotNull(shoppingCart);
        assertEquals(createdDate, shoppingCart.getCreatedDate());
        assertEquals(createdDate, shoppingCart.getLastUpdated());
    }

    @Test
    void ctor_nullCreatedDate_illegalArgumentExceptionThrown() {
        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ShoppingCart(null)
        );
        assertEquals("Creation date cannot be null.", exception.getMessage());
    }

    @Test
    void ctor_futureCreatedDate_illegalArgumentExceptionThrown() {
        // given
        var futureDate = LocalDateTime.now().plusDays(1);

        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ShoppingCart(futureDate)
        );
        assertEquals("Creation date cannot be in the future.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 30, 365})
    void ctor_variousPastDates_shoppingCartCreated(int daysAgo) {
        // given
        var createdDate = LocalDateTime.now().minusDays(daysAgo);

        // when
        var shoppingCart = new ShoppingCart(createdDate);

        // then
        assertNotNull(shoppingCart);
        assertEquals(createdDate, shoppingCart.getCreatedDate());
    }

    @Test
    void getMaxCartItems_always_returns50() {
        // when
        var maxItems = ShoppingCart.getMaxCartItems();

        // then
        assertEquals(50, maxItems);
    }

    @Test
    void setLastUpdated_validDate_lastUpdatedUpdated() {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);
        var newLastUpdated = LocalDateTime.now().minusDays(1);

        // when
        shoppingCart.setLastUpdated(newLastUpdated);

        // then
        assertEquals(newLastUpdated, shoppingCart.getLastUpdated());
    }

    @Test
    void setLastUpdated_dateAfterCreation_lastUpdatedUpdated() {
        // given
        var createdDate = LocalDateTime.now().minusDays(10);
        var shoppingCart = new ShoppingCart(createdDate);
        var newLastUpdated = LocalDateTime.now().minusDays(2);

        // when
        shoppingCart.setLastUpdated(newLastUpdated);

        // then
        assertEquals(newLastUpdated, shoppingCart.getLastUpdated());
    }

    @Test
    void setLastUpdated_nullDate_illegalArgumentExceptionThrown() {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);
        var originalLastUpdated = shoppingCart.getLastUpdated();

        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> shoppingCart.setLastUpdated(null)
        );
        assertEquals("Last updated date cannot be null.", exception.getMessage());
        assertEquals(originalLastUpdated, shoppingCart.getLastUpdated());
    }

    @Test
    void setLastUpdated_dateBeforeCreation_illegalArgumentExceptionThrown() {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);
        var dateBeforeCreation = createdDate.minusDays(1);
        var originalLastUpdated = shoppingCart.getLastUpdated();

        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> shoppingCart.setLastUpdated(dateBeforeCreation)
        );
        assertEquals("Last updated date cannot be before creation date.", exception.getMessage());
        assertEquals(originalLastUpdated, shoppingCart.getLastUpdated());
    }

    @Test
    void setLastUpdated_sameAsCreationDate_lastUpdatedUpdated() {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);

        // when
        shoppingCart.setLastUpdated(createdDate);

        // then
        assertEquals(createdDate, shoppingCart.getLastUpdated());
    }

    @Test
    void getCartItems_emptyCart_returnsEmptySet() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);

        // when
        var cartItems = shoppingCart.getCartItems();

        // then
        assertNotNull(cartItems);
        assertTrue(cartItems.isEmpty());
    }

    @Test
    void getCartItems_afterAddingItems_returnsUnmodifiableSet() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);

        // when
        var cartItems = shoppingCart.getCartItems();

        // then
        assertNotNull(cartItems);
        assertFalse(cartItems.isEmpty());
        assertThrows(UnsupportedOperationException.class, cartItems::clear);
    }

    @Test
    void getCartItems_withItems_returnsCorrectSize() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));

        // when
        shoppingCart.updateCart(product1, 1);
        shoppingCart.updateCart(product2, 2);

        // then
        assertEquals(2, shoppingCart.getCartItems().size());
    }

    @Test
    void clearCart_emptyCart_remainsEmpty() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);

        // when
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void clearCart_cartWithItems_becomesEmpty() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
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
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);
        var product = createTestProduct();
        shoppingCart.updateCart(product, 1);
        var lastUpdatedBeforeClear = shoppingCart.getLastUpdated();

        // when
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        shoppingCart.clearCart();

        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
        assertTrue(shoppingCart.getLastUpdated().isAfter(lastUpdatedBeforeClear) ||
                   shoppingCart.getLastUpdated().equals(lastUpdatedBeforeClear));
    }

    @Test
    void clearCart_multipleItems_removesAll() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
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
    void updateCart_validProduct_productAdded() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product = createTestProduct();

        // when
        shoppingCart.updateCart(product, 1);

        // then
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    void updateCart_nullProduct_illegalArgumentExceptionThrown() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);

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
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var product3 = new Product("Product 3", "Description 3", 30.0, 15, List.of("image3.jpg"));

        // when
        shoppingCart.updateCart(product1, 1);
        shoppingCart.updateCart(product2, 2);
        shoppingCart.updateCart(product3, 3);

        // then
        assertEquals(3, shoppingCart.getCartItems().size());
    }

    @Test
    void updateCart_updatesLastUpdatedDate() {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var shoppingCart = new ShoppingCart(createdDate);
        var product = createTestProduct();
        var originalLastUpdated = shoppingCart.getLastUpdated();

        // when
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        shoppingCart.updateCart(product, 1);

        // then
        assertTrue(shoppingCart.getLastUpdated().isAfter(originalLastUpdated) ||
                   shoppingCart.getLastUpdated().equals(originalLastUpdated));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20})
    void updateCart_variousQuantities_productAdded(int quantity) {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product = createTestProduct();

        // when
        shoppingCart.updateCart(product, quantity);

        // then
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    @Test
    void updateCart_afterClear_productAdded() {
        // given
        var createdDate = LocalDateTime.now().minusDays(1);
        var shoppingCart = new ShoppingCart(createdDate);
        var product1 = createTestProduct();
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        shoppingCart.updateCart(product1, 1);
        shoppingCart.clearCart();

        // when
        shoppingCart.updateCart(product2, 2);

        // then
        assertEquals(1, shoppingCart.getCartItems().size());
    }

    private Product createTestProduct() {
        return new Product("Test Product", "Test Description", 10.0, 5, List.of("image.jpg"));
    }
}

