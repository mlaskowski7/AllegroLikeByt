package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class RegularUser extends User {
    protected List<String> shippingAdresses = new ArrayList<String>();

    protected RegularUser(String username, String email) {
        super(username, email);
    }

    private ShoppingCart shoppingCart;

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public List<String> getShippingAdresses() {
        return shippingAdresses;
    }

    public void setShippingAdresses(List<String> shippingAdresses) {
        this.shippingAdresses = shippingAdresses;
    }

    public void addFunds(double amount) {
        System.out.println("Funds added: " + amount);
    }

    public void purchasePlan(String planName) {
        System.out.println("Plan purchased: " + planName);
    }

    public void reviewProduct(Product product, String review) {
        System.out.println("Product reviewed: " + product + " with review: " + review);
    }
}
