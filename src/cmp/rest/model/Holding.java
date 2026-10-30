package cmp.rest.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Holding {

    private int holdingId;
    private int accountId;
    private int stockId;
    private int quantity;
    private BigDecimal averagePrice;

    public Holding(int holdingId, int accountId, int stockId) {
        this.holdingId = holdingId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.quantity = 0;
        this.averagePrice = BigDecimal.ZERO;
    }

    public void addShares(int shares, BigDecimal price) {

        if (shares <= 0 || price == null ||
            price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid share details");
        }

        BigDecimal oldValue = averagePrice.multiply(
                BigDecimal.valueOf(quantity));

        BigDecimal newValue = price.multiply(
                BigDecimal.valueOf(shares));

        int totalQuantity = quantity + shares;

        averagePrice = oldValue.add(newValue)
                .divide(BigDecimal.valueOf(totalQuantity),
                        2, RoundingMode.HALF_UP);

        quantity = totalQuantity;
    }

    public void removeShares(int shares) {

        if (shares <= 0 || shares > quantity) {
            throw new IllegalArgumentException("Invalid sell quantity");
        }

        quantity -= shares;

        if (quantity == 0) {
            averagePrice = BigDecimal.ZERO;
        }
    }

    public int getHoldingId() {
        return holdingId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getStockId() {
        return stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
}