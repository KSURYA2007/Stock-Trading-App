package stocktrading;

public class Watchlist {
    private final int watchlistId;
    private final int userId;
    private String name;

    public Watchlist(int watchlistId, int userId, String name) {
        this.watchlistId = watchlistId;
        this.userId = userId;
        this.name = name;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
