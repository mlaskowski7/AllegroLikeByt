package pl.edu.pjwstk.byt;

import pl.edu.pjwstk.byt.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.pjwstk.byt.utils.StringUtils.isNullOrBlank;

public class Category {

    private String name;

    private String description;

    private Category parentCategory;

    private List<Category> subCategories;

    public Category(String name, String description, Category parentCategory) {
        if (isNullOrBlank(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (isNullOrBlank(description)) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;

        this.subCategories = new ArrayList<>();
    }

    public void AddSubcategory(Category subcategory) {
        if (subcategory == null) {
            throw new IllegalArgumentException("Subcategory cannot be null");
        }
        if (subcategory == this) {
            throw new IllegalArgumentException("Category cannot be a subcategory of itself");
        }

        subcategory.setParentCategory(this);
        subCategories.add(subcategory);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isNullOrBlank(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (isNullOrBlank(description)) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }
}
