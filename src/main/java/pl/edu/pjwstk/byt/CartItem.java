package pl.edu.pjwstk.byt;

public class CartItem {

    private final Product product;

    private int quantity;

    public CartItem(int quantity, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cart item must have a product");
        }

        this.quantity = quantity;
        this.product = product;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
