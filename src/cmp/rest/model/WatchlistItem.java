package cmp.rest.model;

import java.time.LocalDateTime;

public class WatchlistItem {

    private int itemId;
    private int watchlistId;
    private int stockId;
    private LocalDateTime addedAt;

    public WatchlistItem(int itemId, int watchlistId, int stockId) {
        this.itemId = itemId;
        this.watchlistId = watchlistId;
        this.stockId = stockId;
        this.addedAt = LocalDateTime.now();
    }

    public int getItemId() {
        return itemId;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public int getStockId() {
        return stockId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}