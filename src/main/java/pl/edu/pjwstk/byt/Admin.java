package pl.edu.pjwstk.byt;

public class Admin extends User {

    protected Admin(String username, String email) {
        super(username, email);

    }

    public int getUserCount(){
        return User.usercount;
    }
    public void accessDashboard(){

    }
}
