package pl.edu.pjwstk.byt;

import java.util.Date;
import java.util.List;

import java.io.*;
import java.util.ArrayList;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXTENT_FILE = "User_extent.ser";
    private static List<User> extent = new ArrayList<>();

    private String username;
    private String password;
    private String email;
    private Date registrationDate;
    private boolean isActive;
    private List<Integer> phoneNumber;
    public static int usercount = 0;

    protected User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = new Date();
        this.isActive = true;
        usercount++;
        extent.add(this);
    }

    public static List<User> getExtent() {
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
            extent = (List<User>) ois.readObject();
        }
    }

    // Overloaded constructor for compatibility if needed, using default password
    protected User(String username, String email) {
        this(username, email, "defaultPass123");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Integer> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<Integer> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Overloaded constructor for compatibility if needed, using default password
    protected User(String username, String email) {
        this(username, email, "defaultPass123");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Integer> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<Integer> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
