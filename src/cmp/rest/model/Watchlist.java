package cmp.rest.model;

import java.util.ArrayList;
import java.util.List;

public class Watchlist {

    private int watchlistId;
    private int accountId;
    private String name;
    private List<WatchlistItem> items;

    public Watchlist(int watchlistId, int accountId, String name) {
        this.watchlistId = watchlistId;
        this.accountId = accountId;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(WatchlistItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        items.add(item);
    }

    public void removeItem(int stockId) {
        items.removeIf(item -> item.getStockId() == stockId);
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public List<WatchlistItem> getItems() {
        return new ArrayList<>(items);
    }
}