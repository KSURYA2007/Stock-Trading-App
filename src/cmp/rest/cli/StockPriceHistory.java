package cmp.rest.cli;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockPriceHistory {
    private final int priceId;
    private final int stockId;
    private final BigDecimal price;
    private final LocalDateTime updatedAt;
    private final int updatedBy;

    public StockPriceHistory(int priceId, int stockId, BigDecimal price,
                             LocalDateTime updatedAt, int updatedBy) {
        this.priceId = priceId;
        this.stockId = stockId;
        this.price = price;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public int getPriceId() { return priceId; }
    public int getStockId() { return stockId; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public int getUpdatedBy() { return updatedBy; }
}
