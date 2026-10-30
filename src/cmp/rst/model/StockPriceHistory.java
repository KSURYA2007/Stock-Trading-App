package cmp.rst.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockPriceHistory {

    private int historyId;
    private int stockId;
    private BigDecimal price;
    private LocalDateTime recordedAt;

    public StockPriceHistory(int historyId, int stockId,
                             BigDecimal price) {

        if (price == null ||
            price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid stock price");
        }

        this.historyId = historyId;
        this.stockId = stockId;
        this.price = price;
        this.recordedAt = LocalDateTime.now();
    }

    public int getHistoryId() {
        return historyId;
    }

    public int getStockId() {
        return stockId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}