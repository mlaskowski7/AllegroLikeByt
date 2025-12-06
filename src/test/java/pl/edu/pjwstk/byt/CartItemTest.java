package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemTest {

    @Test
    void ctor_validParameters_cartItemCreated() {
        // given
        var quantity = 5;
        var product = createTestProduct();

        // when
        var cartItem = new CartItem(quantity, product, new ShoppingCart());

        // then
        assertNotNull(cartItem);
        assertEquals(quantity, cartItem.getQuantity());
        assertEquals(product, cartItem.getProduct());
    }

    @Test
    void ctor_zeroQuantity_cartItemCreated() {
        // given
        var quantity = 0;
        var product = createTestProduct();

        // when
        var cartItem = new CartItem(quantity, product, new ShoppingCart());

        // then
        assertNotNull(cartItem);
        assertEquals(quantity, cartItem.getQuantity());
    }

    @Test
    void ctor_negativeQuantity_cartItemCreated() {
        // given
        var quantity = -5;
        var product = createTestProduct();

        // when
        var cartItem = new CartItem(quantity, product, new ShoppingCart());

        // then
        assertNotNull(cartItem);
        assertEquals(quantity, cartItem.getQuantity());
    }

    @Test
    void ctor_nullProduct_illegalArgumentExceptionThrown() {
        // given
        var quantity = 5;

        // then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CartItem(quantity, null, new ShoppingCart())
        );
        assertEquals("Cart item must have a product", exception.getMessage());
    }

    @Test
    void ctor_nullCart_illegalArgumentExceptionThrown() {
        // given
        var quantity = 5;

        // then
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(quantity, createTestProduct(), null)
        );
        assertEquals("Cart item must be instantiated with a shopping cart", exception.getMessage());
    }

    @Test
    void updateQuantity_validPositiveQuantity_quantityUpdated() {
        // given
        var product = createTestProduct();
        var cartItem = new CartItem(5, product, new ShoppingCart());
        var newQuantity = 10;

        // when
        cartItem.updateQuantity(newQuantity);

        // then
        assertEquals(newQuantity, cartItem.getQuantity());
    }

    @Test
    void updateQuantity_zeroQuantity_quantityUpdated() {
        // given
        var product = createTestProduct();
        var cartItem = new CartItem(5, product, new ShoppingCart());
        var newQuantity = 0;

        // when
        cartItem.updateQuantity(newQuantity);

        // then
        assertEquals(newQuantity, cartItem.getQuantity());
    }

    @Test
    void updateQuantity_negativeQuantity_quantityUpdated() {
        // given
        var product = createTestProduct();
        var cartItem = new CartItem(5, product, new ShoppingCart());
        var newQuantity = -3;

        // when
        cartItem.updateQuantity(newQuantity);

        // then
        assertEquals(newQuantity, cartItem.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 50, 100, 1000})
    void updateQuantity_variousPositiveQuantities_quantityUpdated(int newQuantity) {
        // given
        var product = createTestProduct();
        var cartItem = new CartItem(1, product, new ShoppingCart());

        // when
        cartItem.updateQuantity(newQuantity);

        // then
        assertEquals(newQuantity, cartItem.getQuantity());
    }

    @Test
    void updateQuantity_multipleTimes_quantityUpdatedEachTime() {
        // given
        var product = createTestProduct();
        var cartItem = new CartItem(1, product, new ShoppingCart());

        // then
        cartItem.updateQuantity(5);
        assertEquals(5, cartItem.getQuantity());

        cartItem.updateQuantity(10);
        assertEquals(10, cartItem.getQuantity());

        cartItem.updateQuantity(3);
        assertEquals(3, cartItem.getQuantity());

        cartItem.updateQuantity(0);
        assertEquals(0, cartItem.getQuantity());
    }

    @Test
    void getQuantity_afterCreation_returnsCorrectQuantity() {
        // given
        var quantity = 15;
        var product = createTestProduct();
        var cartItem = new CartItem(quantity, product, new ShoppingCart());

        // when
        var result = cartItem.getQuantity();

        // then
        assertEquals(quantity, result);
    }

    @Test
    void getProduct_afterCreation_returnsCorrectProduct() {
        // given
        var quantity = 5;
        var product = createTestProduct();
        var cartItem = new CartItem(quantity, product, new ShoppingCart());

        // when
        var result = cartItem.getProduct();

        // then
        assertNotNull(result);
        assertEquals(product, result);
        assertSame(product, result);
    }

    @Test
    void getProduct_sameProductForMultipleCartItems_returnsCorrectProduct() {
        // given
        var product = createTestProduct();
        var cartItem1 = new CartItem(5, product, new ShoppingCart());
        var cartItem2 = new CartItem(10, product, new ShoppingCart());

        // when
        var result1 = cartItem1.getProduct();
        var result2 = cartItem2.getProduct();

        // then
        assertSame(product, result1);
        assertSame(product, result2);
        assertSame(result1, result2);
    }

    @Test
    void getProduct_differentProducts_returnsDifferentProducts() {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var cartItem1 = new CartItem(5, product1, new ShoppingCart());
        var cartItem2 = new CartItem(10, product2, new ShoppingCart());

        // when
        var result1 = cartItem1.getProduct();
        var result2 = cartItem2.getProduct();

        // then
        assertNotSame(result1, result2);
        assertSame(product1, result1);
        assertSame(product2, result2);
    }

    private Product createTestProduct() {
        return new Product("Test Product", "Test Description", 10.0, 5, List.of("image.jpg"));
    }
}

