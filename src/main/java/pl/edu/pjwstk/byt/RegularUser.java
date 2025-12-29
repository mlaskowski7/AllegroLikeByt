package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class RegularUser extends User {

    private List<Address> shippingAddresses;
    private List<Order> orders;
    private ShoppingCart shoppingCart;

    private static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return username;
    }

    private static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return email;
    }

    public RegularUser(String username, String email) {
        super(validateUsername(username), validateEmail(email));
        this.shippingAddresses = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.shoppingCart = new ShoppingCart();
    }

    // ----------------------
    // Shipping Address Methods
    // ----------------------
    public void addShippingAddress(Address address) {
        if (address != null) {
            shippingAddresses.add(address);
        }
    }

    /**
     * @deprecated Use {@link #addShippingAddress(Address)} instead.
     */
    @Deprecated
    public void addShippingAdress(Address adress) {
        addShippingAddress(adress);
    }

    public void removeShippingAddress(Address address) {
        shippingAddresses.remove(address);
    }

    /**
     * @deprecated Use {@link #removeShippingAddress(Address)} instead.
     */
    @Deprecated
    public void removeShippingAdress(Address adress) {
        removeShippingAddress(adress);
    }

    public List<Address> getAddressList() {
        return new ArrayList<>(shippingAddresses);
    }

    /**
     * @deprecated Use {@link #getAddressList()} instead.
     */
    @Deprecated
    public List<Address> getAdressList() {
        return getAddressList();
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
                // Since Order requires a RegularUser (multiplicity 1), removing it from RegularUser
                // implies the Order execution (lifecycle) is over or it's being deleted.
                order.delete();
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

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /**
     * Returns a list of formatted shipping addresses containing full address information.
     * Each string includes street, city, zip code, and country.
     * 
     * @return list of formatted address strings
     */
    public List<String> getShippingAddresses() {
        List<String> addresses = new ArrayList<>();
        for (Address addr : shippingAddresses) {
            addresses.add(addr.formatForShipping());
        }
        return addresses;
    }

    /**
     * @deprecated Use {@link #getShippingAddresses()} instead.
     */
    @Deprecated
    public List<String> getShippingAdresses() {
        return getShippingAddresses();
    }

    /**
     * Sets the shipping addresses using full {@link Address} domain objects.
     * This replaces all currently stored shipping addresses.
     *
     * @param addresses list of {@link Address} instances to set; if {@code null}, the list is cleared
     */
    public void setShippingAddresses(List<Address> addresses) {
        this.shippingAddresses.clear();
        if (addresses != null) {
            this.shippingAddresses.addAll(addresses);
        }
    }

    /**
     * @deprecated Use {@link #setShippingAddresses(List)} instead.
     */
    @Deprecated
    public void setShippingAdresses(List<Address> addresses) {
        setShippingAddresses(addresses);
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

