package pl.edu.pjwstk.byt;

import java.util.Date;
import java.util.List;

public abstract class User {
    private String username;
    private String email;
    private Date registrationDate;
    private boolean isActive;
    private List<Integer> phoneNumber;
    public static int usercount=0;
    protected User(String username, String email) {
        this.username = username;
        this.email = email;
        this.registrationDate = new Date();
        this.isActive = true;
        usercount++;
    }

}
