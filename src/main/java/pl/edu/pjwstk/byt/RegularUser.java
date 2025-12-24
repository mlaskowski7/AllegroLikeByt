package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class RegularUser extends User {

    private List<Adress> shippingAdresses;
    private List<Order> orders;
    private ShoppingCart shoppingCart;

    public RegularUser(String username, String email) {
        super(username, email);
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty");

        this.shippingAdresses = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.shoppingCart = new ShoppingCart();
    }

    // ----------------------
    // Shipping Address Methods
    // ----------------------
    public void addShippingAdress(Adress adress) {
        if (adress != null) {
            shippingAdresses.add(adress);
        }
    }

    public void removeShippingAdress(Adress adress) {
        shippingAdresses.remove(adress);
    }

    public List<Adress> getShippingAdresses() {
        return new ArrayList<>(shippingAdresses);
    }

    // ----------------------
// Order Methods (with bidirectional linking)
// ----------------------
    public void addOrder(Order order) {
        if (order != null && !orders.contains(order)) {
            orders.add(order);
            // maintain bidirectional link
            if (order.getCustomer() != this) {
                order.setCustomer(this);
            }
        }
    }

    // Internal method for Order to call safely
    protected void addOrderInternal(Order order) {
        if (order != null && !orders.contains(order)) {
            orders.add(order);
        }
    }

    public void removeOrder(Order order) {
        if (order != null && orders.contains(order)) {
            orders.remove(order);
            if (order.getCustomer() == this) {
                order.setCustomer(null);
            }
        }
    }


    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void clearShoppingCart() {
        shoppingCart.clearCart();
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
