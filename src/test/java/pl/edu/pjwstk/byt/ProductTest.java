package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    // Basic attribute (name) TESTS
    @Test
    void shouldCreateProductWithValidName() {
        var product = new Product("Phone", "Smartphone", 1500.0, 5, List.of("img1.jpg"));
        assertEquals("Phone", product.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("", "Smartphone", 1500.0, 5, List.of("img1.jpg"))
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(null, "Smartphone", 1500.0, 5, List.of("img1.jpg"))
        );
    }

    // Multi-value attribute (images) TESTS
    @Test
    void shouldAcceptMultipleImages() {
        var images = List.of("front.jpg", "back.jpg");
        var product = new Product("Camera", "DSLR", 2500.0, 10, images);
        assertEquals(2, product.getImages().size());
    }


    @Test
    void shouldThrowExceptionWhenImagesListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Camera", "DSLR", 2500.0, 10, List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenImagesListIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Camera", "DSLR", 2500.0, 10, null)
        );
    }

    // Derived attribute (avgRating) TESTS
    @Test
    void shouldCalculateAverageRatingCorrectly() {
        var product = new Product("Laptop", "Gaming laptop", 4000.0, 5, List.of("img1.jpg"));
        product.addReview(5);
        product.addReview(3);
        assertEquals(4.0, product.getAvgRating());
    }

    @Test
    void shouldThrowExceptionWhenRatingOutOfRange() {
        var product = new Product("Laptop", "Gaming laptop", 4000.0, 5, List.of("img1.jpg"));
        assertThrows(IllegalArgumentException.class, () -> product.addReview(6));
    }

    @Test
    void shouldReturnZeroWhenNoRatingsGiven() {
        var product = new Product("Mouse", "Wireless", 100.0, 10, List.of("img1.jpg"));
        assertEquals(0.0, product.getAvgRating());
    }
}
