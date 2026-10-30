package stocktrading;

import java.time.LocalDateTime;

public class WatchlistItem {
    private final int itemId;
    private final int watchlistId;
    private final int stockId;
    private final LocalDateTime addedAt;

    public WatchlistItem(int itemId, int watchlistId, int stockId, LocalDateTime addedAt) {
        this.itemId = itemId;
        this.watchlistId = watchlistId;
        this.stockId = stockId;
        this.addedAt = addedAt;
    }

    public int getItemId() { return itemId; }
    public int getWatchlistId() { return watchlistId; }
    public int getStockId() { return stockId; }
    public LocalDateTime getAddedAt() { return addedAt; }
}
