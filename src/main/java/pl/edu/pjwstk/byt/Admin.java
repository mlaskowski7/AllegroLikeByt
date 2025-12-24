package pl.edu.pjwstk.byt;

public class Admin extends User {

    protected Admin(String username, String email) {
        super(username, email);

    }

    public int getUserCount() {
        return User.usercount;
    }

    public void accessDashboard() {
        System.out.println("Accessing dashboard...");
    }

    public void grantAccess(User user) {
        if (user != null) {
            user.setActive(true);
            System.out.println("Access granted to user: " + user.getUsername());
        }
    }

    public void revokeAccess(User user) {
        if (user != null) {
            user.setActive(false);
            System.out.println("Access revoked for user: " + user.getUsername());
        }
    }
}
