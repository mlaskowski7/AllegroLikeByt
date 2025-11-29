package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    void ctor_validParametersMissingCategory_categoryCreated() {
        // given
        var name = "Electronics";
        var description = "Electronic devices and accessories";

        // when
        var category = new Category(name, description, null);

        // then
        assertNotNull(category);
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertNull(category.getParentCategory());
        assertNotNull(category.getSubCategories());
        assertTrue(category.getSubCategories().isEmpty());
    }

    @Test
    void ctor_parentCategoryPresent_categoryCreatedWithParentCategory() {
        // given
        var parentCategory = new Category("Parent", "Parent description", null);
        var name = "Child";
        var description = "Child description";

        // when
        var category = new Category(name, description, parentCategory);

        // then
        assertNotNull(category);
        assertEquals(parentCategory, category.getParentCategory());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    void ctor_invalidName_illegalArgumentExceptionThrown(String invalidName) {
        // given
        var description = "Valid description";

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Category(invalidName, description, null)
        );
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    void ctor_invalidDescription_illegalArgumentExceptionThrown(String invalidDescription) {
        // given
        var name = "Valid name";

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Category(name, invalidDescription, null)
        );
        assertEquals("Description cannot be null or empty", exception.getMessage());
    }

    // Setter Tests - Name

    @Test
    void setName_validName_nameUpdated() {
        // given
        var category = new Category("Original", "Description", null);
        var newName = "Updated Name";

        // when
        category.setName(newName);

        // then
        assertEquals(newName, category.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    void setName_invalidName_illegalArgumentExceptionThrown(String invalidName) {
        // given
        var category = new Category("Original", "Description", null);
        var originalName = category.getName();

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> category.setName(invalidName)
        );
        assertEquals("Name cannot be null or empty", exception.getMessage());
        assertEquals(originalName, category.getName());
    }

    // Setter Tests - Description

    @Test
    void setDescription_validDescription_descriptionUpdated() {
        // given
        var category = new Category("Name", "Original description", null);
        var newDescription = "Updated description";

        // when
        category.setDescription(newDescription);

        // then
        assertEquals(newDescription, category.getDescription());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "  "})
    void setDescription_invalidDescription_illegalArgumentExceptionThrown(String invalidDescription) {
        // given
        var category = new Category("Name", "Original description", null);
        var originalDescription = category.getDescription();

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> category.setDescription(invalidDescription)
        );
        assertEquals("Description cannot be null or empty", exception.getMessage());
        assertEquals(originalDescription, category.getDescription());
    }

    // Setter Tests - Parent Category

    @Test
    void setParentCategory_validParent_parentUpdated() {
        // given
        var category = new Category("Child", "Child description", null);
        var parentCategory = new Category("Parent", "Parent description", null);

        // when
        category.setParentCategory(parentCategory);

        // then
        assertEquals(parentCategory, category.getParentCategory());
    }

    @Test
    void setParentCategory_null_parentRemoved() {
        // given
        var parentCategory = new Category("Parent", "Parent description", null);
        var category = new Category("Child", "Child description", parentCategory);

        // when
        category.setParentCategory(null);

        // then
        assertNull(category.getParentCategory());
    }

    // AddSubcategory Tests

    @Test
    void addSubcategory_validSubcategory_subcategoryAdded() {
        // given
        var parentCategory = new Category("Parent", "Parent description", null);
        var subcategory = new Category("Child", "Child description", null);

        // when
        parentCategory.AddSubcategory(subcategory);

        // then
        assertEquals(1, parentCategory.getSubCategories().size());
        assertTrue(parentCategory.getSubCategories().contains(subcategory));
        assertEquals(parentCategory, subcategory.getParentCategory());
    }

    @Test
    void addSubcategory_multipleSubcategories_allSubcategoriesAdded() {
        // given
        var parentCategory = new Category("Parent", "Parent description", null);
        var subcategory1 = new Category("Child1", "Child1 description", null);
        var subcategory2 = new Category("Child2", "Child2 description", null);
        var subcategory3 = new Category("Child3", "Child3 description", null);

        // when
        parentCategory.AddSubcategory(subcategory1);
        parentCategory.AddSubcategory(subcategory2);
        parentCategory.AddSubcategory(subcategory3);

        // then
        assertEquals(3, parentCategory.getSubCategories().size());
        assertTrue(parentCategory.getSubCategories().contains(subcategory1));
        assertTrue(parentCategory.getSubCategories().contains(subcategory2));
        assertTrue(parentCategory.getSubCategories().contains(subcategory3));
        assertEquals(parentCategory, subcategory1.getParentCategory());
        assertEquals(parentCategory, subcategory2.getParentCategory());
        assertEquals(parentCategory, subcategory3.getParentCategory());
    }

    @Test
    void addSubcategory_null_illegalArgumentExceptionThrown() {
        // given
        var parentCategory = new Category("Parent", "Parent description", null);

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> parentCategory.AddSubcategory(null)
        );
        assertEquals("Subcategory cannot be null", exception.getMessage());
        assertTrue(parentCategory.getSubCategories().isEmpty());
    }

    @Test
    void addSubcategory_itself_illegalArgumentExceptionThrown() {
        // given
        var category = new Category("Category", "Description", null);

        // when & then
        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> category.AddSubcategory(category)
        );
        assertEquals("Category cannot be a subcategory of itself", exception.getMessage());
        assertTrue(category.getSubCategories().isEmpty());
    }

    @Test
    void addSubcategory_subcategoryWithExistingParent_parentUpdated() {
        // given
        var oldParent = new Category("Old Parent", "Old parent description", null);
        var newParent = new Category("New Parent", "New parent description", null);
        var subcategory = new Category("Child", "Child description", oldParent);

        // when
        newParent.AddSubcategory(subcategory);

        // then
        assertEquals(newParent, subcategory.getParentCategory());
        assertEquals(1, newParent.getSubCategories().size());
        assertTrue(newParent.getSubCategories().contains(subcategory));
    }

    // Setter Tests - SubCategories

    @Test
    void setSubCategories_validList_subcategoriesUpdated() {
        // given
        var category = new Category("Parent", "Parent description", null);
        var sub1 = new Category("Child1", "Child1 description", null);
        var sub2 = new Category("Child2", "Child2 description", null);
        var subcategories = new ArrayList<Category>();
        subcategories.add(sub1);
        subcategories.add(sub2);

        // when
        category.setSubCategories(subcategories);

        // then
        assertEquals(2, category.getSubCategories().size());
        assertTrue(category.getSubCategories().contains(sub1));
        assertTrue(category.getSubCategories().contains(sub2));
    }

    @Test
    void setSubCategories_emptyList_subcategoriesCleared() {
        // given
        var category = new Category("Parent", "Parent description", null);
        var sub1 = new Category("Child1", "Child1 description", null);
        category.AddSubcategory(sub1);
        var emptyList = new ArrayList<Category>();

        // when
        category.setSubCategories(emptyList);

        // then
        assertTrue(category.getSubCategories().isEmpty());
    }

    @Test
    void setSubCategories_null_subcategoriesSetToNull() {
        // given
        var category = new Category("Parent", "Parent description", null);
        var sub1 = new Category("Child1", "Child1 description", null);
        category.AddSubcategory(sub1);

        // when
        category.setSubCategories(null);

        // then
        assertNull(category.getSubCategories());
    }

    // Getter Tests

    @Test
    void getSubCategories_categoryWithSubcategories_returnsSubcategoriesList() {
        // given
        var category = new Category("Parent", "Parent description", null);
        var sub1 = new Category("Child1", "Child1 description", null);
        var sub2 = new Category("Child2", "Child2 description", null);
        category.AddSubcategory(sub1);
        category.AddSubcategory(sub2);

        // when
        var subcategories = category.getSubCategories();

        // then
        assertNotNull(subcategories);
        assertEquals(2, subcategories.size());
        assertTrue(subcategories.contains(sub1));
        assertTrue(subcategories.contains(sub2));
    }

    @Test
    void getSubCategories_categoryWithoutSubcategories_returnsEmptyList() {
        // given
        var category = new Category("Parent", "Parent description", null);

        // when
        var subcategories = category.getSubCategories();

        // then
        assertNotNull(subcategories);
        assertTrue(subcategories.isEmpty());
    }
}
