package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemExtentTest {

    private static final String EXTENT_FILE = "CartItem_extent.ser";

    @BeforeEach
    void setUp() {
        // Clear extent before each test
        CartItem.clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        CartItem.clearExtent();
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getExtent_afterCreatingCartItems_returnsAllCartItems() {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var cartItem1 = new CartItem(2, product1);
        var cartItem2 = new CartItem(3, product2);
        var cartItem3 = new CartItem(1, product1);

        // when
        var extent = CartItem.getExtent();

        // then
        assertEquals(3, extent.size());
        assertTrue(extent.contains(cartItem1));
        assertTrue(extent.contains(cartItem2));
        assertTrue(extent.contains(cartItem3));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        // when
        var extent = CartItem.getExtent();

        // then
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var product = new Product("Product", "Description", 10.0, 5, List.of("image.jpg"));
        var cartItem = new CartItem(1, product);
        var extent1 = CartItem.getExtent();
        int originalSize = extent1.size();

        // when
        extent1.clear(); // This should not affect the original extent
        var extent2 = CartItem.getExtent();

        // then
        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(cartItem));
    }

    @Test
    void saveExtent_afterCreatingCartItems_savesToFile() throws IOException {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var cartItem1 = new CartItem(2, product1);
        var cartItem2 = new CartItem(3, product2);

        // when
        CartItem.saveExtent();

        // then
        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllCartItems() throws IOException, ClassNotFoundException {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var cartItem1 = new CartItem(2, product1);
        var cartItem2 = new CartItem(3, product2);
        CartItem.saveExtent();

        // Clear extent
        CartItem.clearExtent();

        // when
        CartItem.loadExtent();
        var loadedExtent = CartItem.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        assertEquals(2, loadedExtent.get(0).getQuantity());
        assertEquals(3, loadedExtent.get(1).getQuantity());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // then
        assertThrows(IOException.class, () -> CartItem.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesCartItemAttributes() throws IOException, ClassNotFoundException {
        // given
        var product = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        var cartItem = new CartItem(5, product);
        cartItem.updateQuantity(10);
        CartItem.saveExtent();

        // Clear extent
        CartItem.clearExtent();

        // when
        CartItem.loadExtent();
        var loadedExtent = CartItem.getExtent();

        // then
        assertEquals(1, loadedExtent.size());
        var loadedCartItem = loadedExtent.get(0);
        assertEquals(10, loadedCartItem.getQuantity());
        assertNotNull(loadedCartItem.getProduct());
        assertEquals("Test Product", loadedCartItem.getProduct().getName());
    }
}

