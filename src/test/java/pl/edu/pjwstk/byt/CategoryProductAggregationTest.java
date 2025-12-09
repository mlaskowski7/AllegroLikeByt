package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryProductAggregationTest {

    @Test
    void addProduct_validProduct_productAddedAndReverseConnected() {
        var category = new Category("Electronics", "Devices", null);
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1"));

        category.addProduct(product);

        assertEquals(category, product.getCategory());
        assertTrue(category.getProducts().contains(product));
    }

    @Test
    void addProduct_duplicateProduct_illegalArgumentExceptionThrown() {
        var category = new Category("Electronics", "Devices", null);
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1"));

        category.addProduct(product);

        var exception = assertThrows(IllegalArgumentException.class, () -> category.addProduct(product));
        assertEquals("Product already added to this category", exception.getMessage());
    }

    @Test
    void addProduct_productBelongsToOtherCategory_illegalArgumentExceptionThrown() {
        var cat1 = new Category("Cat1", "Desc1", null);
        var cat2 = new Category("Cat2", "Desc2", null);
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1"));
        cat1.addProduct(product);

        var exception = assertThrows(IllegalArgumentException.class, () -> cat2.addProduct(product));
        assertEquals("Product already belongs to another category", exception.getMessage());
    }

    @Test
    void removeProduct_validProduct_productRemovedAndReverseCleared() {
        var category = new Category("Electronics", "Devices", null);
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1"));

        category.addProduct(product);
        category.removeProduct(product);

        assertNull(product.getCategory());
        assertFalse(category.getProducts().contains(product));
    }

    @Test
    void removeProduct_productNotInCategory_illegalArgumentExceptionThrown() {
        var category = new Category("Electronics", "Devices", null);
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1"));
        var exception = assertThrows(IllegalArgumentException.class, () -> category.removeProduct(product));
        assertEquals("Product is not in this category", exception.getMessage());
    }
}
