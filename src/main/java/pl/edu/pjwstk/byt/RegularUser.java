package pl.edu.pjwstk.byt;

import java.util.ArrayList;
import java.util.List;

public class RegularUser extends User {
    protected List<String> shippingAdresses = new ArrayList<String>();

    protected RegularUser(String username, String email) {
        super(username, email);
    }
}
