package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryExtentTest {

    private static final String EXTENT_FILE = "Category_extent.ser";

    @BeforeEach
    void setUp() {
        // Clear extent before each test
        Category.clearExtent();
        // Delete persistence file if exists
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        Category.clearExtent();
        File file = new File(EXTENT_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getExtent_afterCreatingCategories_returnsAllCategories() {
        // given
        var category1 = new Category("Electronics", "Electronic devices", null);
        var category2 = new Category("Clothing", "Clothing items", null);
        var category3 = new Category("Books", "Books and magazines", null);

        // when
        var extent = Category.getExtent();

        // then
        assertEquals(3, extent.size());
        assertTrue(extent.contains(category1));
        assertTrue(extent.contains(category2));
        assertTrue(extent.contains(category3));
    }

    @Test
    void getExtent_emptyExtent_returnsEmptyList() {
        // when
        var extent = Category.getExtent();

        // then
        assertNotNull(extent);
        assertTrue(extent.isEmpty());
    }

    @Test
    void getExtent_returnsCopy_notOriginalList() {
        // given
        var category = new Category("Test", "Test Description", null);
        var extent1 = Category.getExtent();
        int originalSize = extent1.size();

        // when
        extent1.clear(); // This should not affect the original extent
        var extent2 = Category.getExtent();

        // then
        assertEquals(originalSize, extent2.size());
        assertTrue(extent2.contains(category));
    }

    @Test
    void saveExtent_afterCreatingCategories_savesToFile() throws IOException {
        // given
        var category1 = new Category("Electronics", "Electronic devices", null);
        var category2 = new Category("Clothing", "Clothing items", null);

        // when
        Category.saveExtent();

        // then
        File file = new File(EXTENT_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void loadExtent_afterSaving_loadsAllCategories() throws IOException, ClassNotFoundException {
        // given
        var category1 = new Category("Electronics", "Electronic devices", null);
        var category2 = new Category("Clothing", "Clothing items", null);
        Category.saveExtent();

        // Clear extent
        Category.clearExtent();

        // when
        Category.loadExtent();
        var loadedExtent = Category.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        assertEquals("Electronics", loadedExtent.get(0).getName());
        assertEquals("Clothing", loadedExtent.get(1).getName());
    }

    @Test
    void loadExtent_fileDoesNotExist_throwsException() {
        // then
        assertThrows(IOException.class, () -> Category.loadExtent());
    }

    @Test
    void saveAndLoadExtent_preservesCategoryAttributes() throws IOException, ClassNotFoundException {
        // given
        var parent = new Category("Parent", "Parent Description", null);
        var child = new Category("Child", "Child Description", parent);
        Category.saveExtent();

        // Clear extent
        Category.clearExtent();

        // when
        Category.loadExtent();
        var loadedExtent = Category.getExtent();

        // then
        assertEquals(2, loadedExtent.size());
        var loadedParent = loadedExtent.stream()
                .filter(c -> c.getName().equals("Parent"))
                .findFirst()
                .orElse(null);
        var loadedChild = loadedExtent.stream()
                .filter(c -> c.getName().equals("Child"))
                .findFirst()
                .orElse(null);
        assertNotNull(loadedParent);
        assertNotNull(loadedChild);
        assertEquals("Parent Description", loadedParent.getDescription());
        assertEquals("Child Description", loadedChild.getDescription());
    }
}

