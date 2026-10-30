package cmp.rest.model;

public class Customer extends User {

    public Customer(int userId, String name,
                    String panNumber, String email,
                    String phone) {

        super(userId, name, panNumber, email, phone);
    }
}