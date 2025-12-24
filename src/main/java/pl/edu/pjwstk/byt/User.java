package pl.edu.pjwstk.byt;

import java.util.Date;
import java.util.List;

public abstract class User {
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
