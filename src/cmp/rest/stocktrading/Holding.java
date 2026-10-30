package cmp.rest.stocktrading;

import java.math.BigDecimal;

public class Holding {
    private final int holdingId;
    private final int accountId;
    private final int stockId;
    private int quantity;
    private BigDecimal averageBuyPrice;

    public Holding(int holdingId, int accountId, int stockId, int quantity, BigDecimal averageBuyPrice) {
        this.holdingId = holdingId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public int getHoldingId() { return holdingId; }
    public int getAccountId() { return accountId; }
    public int getStockId() { return stockId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getAverageBuyPrice() { return averageBuyPrice; }
    public void setAverageBuyPrice(BigDecimal averageBuyPrice) { this.averageBuyPrice = averageBuyPrice; }
}
