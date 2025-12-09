package pl.edu.pjwstk.byt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.pjwstk.byt.utils.StringUtils.isNullOrBlank;

public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "Category_extent.ser";
    private static List<Category> extent = new ArrayList<>();

    private String name;

    private String description;

    private Category parentCategory; // optional attribute, reflex association

    private List<Category> subCategories;
    private List<Product> products = new ArrayList<>();


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
        if (parentCategory != null) {
            parentCategory.addSubcategory(this);
        }

        this.subCategories = new ArrayList<>();
        extent.add(this);
    }
    // product <-> category 0..1 aggregation implementation (exceptions)
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (products.contains(product)) {
            throw new IllegalArgumentException("Product already added to this category");
        }

        if (product.getCategory() != null) {
            throw new IllegalArgumentException("Product already belongs to another category");
        }

        products.add(product);
        product.assignCategory(this); // reverse connection
    }
    public void removeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (!products.contains(product)) {
            throw new IllegalArgumentException("Product is not in this category");
        }

        products.remove(product);
        product.removeCategory(); // reverse connection
    }
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }



    public void addSubcategory(Category subcategory) {
        if (subcategory == null) {
            throw new IllegalArgumentException("Subcategory cannot be null");
        }
        if (subcategory == this) {
            throw new IllegalArgumentException("Category cannot be a subcategory of itself");
        }
        if (subCategories.contains(subcategory)) {
            throw new IllegalArgumentException("Subcategory already registered");
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
        var prevParent = this.parentCategory;
        if (prevParent != null && prevParent != parentCategory) {
            prevParent.getSubCategories().remove(this);
        }

        this.parentCategory = parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public static List<Category> getExtent() {
        return new ArrayList<>(extent);
    }

    public static void saveExtent() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            oos.writeObject(extent);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Category>) ois.readObject();
        }
    }
}
