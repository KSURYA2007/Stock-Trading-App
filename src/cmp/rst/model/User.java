package cmp.rst.model;

public abstract class User {

    private int userId;
    private String name;
    private String panNumber;
    private String email;
    private String phone;

    public User(int userId, String name,
                String panNumber, String email,
                String phone) {

        this.userId = userId;
        this.name = name;
        this.panNumber = panNumber;
        this.email = email;
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}