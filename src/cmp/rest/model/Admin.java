package cmp.rest.model;

public class Admin extends User {

    public Admin(int userId, String name,
                 String panNumber, String email,
                 String phone) {

        super(userId, name, panNumber, email, phone);
    }
}