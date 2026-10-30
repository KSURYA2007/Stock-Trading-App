package cmp.rest.stocktrading;

public abstract class Account {
    private final int id;
    private int userId;

    protected Account(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public abstract String getAccountType();
}
