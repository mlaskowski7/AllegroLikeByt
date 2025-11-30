package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartExtentTest {

    private static final String EXTENT_FILE = "ShoppingCart_extent.ser";

    private void clearExtent() throws Exception {
        Field field = ShoppingCart.class.getDeclaredField("extent");
        field.setAccessible(true);
        ((List<?>) field.get(null)).clear();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        clearExtent();
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getExtent_afterCreatingShoppingCarts_returnsAllShoppingCarts() {
        // given
        var cart1 = new ShoppingCart(LocalDateTime.now().minusDays(1));
        var cart2 = new ShoppingCart(LocalDateTime.now().minusDays(2));
        var cart3 = new ShoppingCart(LocalDateTime.now().minusDays(3));

        // when
        var extent = ShoppingCart.getExtent();

        // then
        assertEquals(3, extent.size());
        assertTrue(extent.contains(cart1));
        assertTrue(extent.contains(cart2));
        assertTrue(extent.contains(cart3));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        // when
        var extent = ShoppingCart.getExtent();

        // then
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var cart = new ShoppingCart(LocalDateTime.now().minusDays(1));
        var extent1 = ShoppingCart.getExtent();
        int originalSize = extent1.size();

        // when
        extent1.clear(); // This should not affect the original extent
        var extent2 = ShoppingCart.getExtent();

        // then
        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(cart));
    }

    @Test
    void saveExtent_afterCreatingShoppingCarts_savesToFile() throws IOException {
        // given
        var cart1 = new ShoppingCart(LocalDateTime.now().minusDays(1));
        var cart2 = new ShoppingCart(LocalDateTime.now().minusDays(2));

        // when
        ShoppingCart.saveExtent();

        // then
        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllShoppingCarts() throws Exception {
        // given
        var createdDate1 = LocalDateTime.now().minusDays(1);
        var createdDate2 = LocalDateTime.now().minusDays(2);
        var cart1 = new ShoppingCart(createdDate1);
        var cart2 = new ShoppingCart(createdDate2);
        ShoppingCart.saveExtent();

        clearExtent();

        // when
        ShoppingCart.loadExtent();
        var loadedExtent = ShoppingCart.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        assertNotNull(loadedExtent.get(0).getCreatedDate());
        assertNotNull(loadedExtent.get(1).getCreatedDate());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // then
        assertThrows(IOException.class, () -> ShoppingCart.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesShoppingCartAttributes() throws Exception {
        // given
        var createdDate = LocalDateTime.now().minusDays(5);
        var cart = new ShoppingCart(createdDate);
        var product = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        cart.updateCart(product, 2);
        ShoppingCart.saveExtent();

        clearExtent();

        // when
        ShoppingCart.loadExtent();
        var loadedExtent = ShoppingCart.getExtent();

        // then
        assertEquals(1, loadedExtent.size());
        var loadedCart = loadedExtent.get(0);
        assertEquals(1, loadedCart.getCartItems().size());
        assertNotNull(loadedCart.getCreatedDate());
        assertNotNull(loadedCart.getLastUpdated());
    }
}

