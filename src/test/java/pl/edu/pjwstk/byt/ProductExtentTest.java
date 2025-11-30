package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductExtentTest {

    private static final String EXTENT_FILE = "Product_extent.ser";

    @BeforeEach
    void setUp() {
        // Clear extent before each test
        Product.clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        Product.clearExtent();
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getExtent_afterCreatingProducts_returnsAllProducts() {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        var product3 = new Product("Product 3", "Description 3", 30.0, 15, List.of("image3.jpg"));

        // when
        var extent = Product.getExtent();

        // then
        assertEquals(3, extent.size());
        assertTrue(extent.contains(product1));
        assertTrue(extent.contains(product2));
        assertTrue(extent.contains(product3));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        // when
        var extent = Product.getExtent();

        // then
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var product = new Product("Product", "Description", 10.0, 5, List.of("image.jpg"));
        var extent1 = Product.getExtent();
        int originalSize = extent1.size();

        // when
        extent1.clear(); // This should not affect the original extent
        var extent2 = Product.getExtent();

        // then
        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(product));
    }

    @Test
    void saveExtent_afterCreatingProducts_savesToFile() throws IOException {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));

        // when
        Product.saveExtent();

        // then
        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllProducts() throws IOException, ClassNotFoundException {
        // given
        var product1 = new Product("Product 1", "Description 1", 10.0, 5, List.of("image1.jpg"));
        var product2 = new Product("Product 2", "Description 2", 20.0, 10, List.of("image2.jpg"));
        Product.saveExtent();

        // Clear extent
        Product.clearExtent();

        // when
        Product.loadExtent();
        var loadedExtent = Product.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        assertEquals("Product 1", loadedExtent.get(0).getName());
        assertEquals("Product 2", loadedExtent.get(1).getName());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // then
        assertThrows(IOException.class, () -> Product.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesProductAttributes() throws IOException, ClassNotFoundException {
        // given
        var product = new Product("Test Product", "Test Description", 15.5, 20, List.of("test.jpg"));
        product.addReview(5);
        product.addReview(4);
        Product.saveExtent();

        // Clear extent
        Product.clearExtent();

        // when
        Product.loadExtent();
        var loadedExtent = Product.getExtent();

        // then
        assertEquals(1, loadedExtent.size());
        var loadedProduct = loadedExtent.get(0);
        assertEquals("Test Product", loadedProduct.getName());
        assertEquals(15.5, loadedProduct.getPrice());
        assertEquals(4.5, loadedProduct.getAvgRating(), 0.01);
    }
}

