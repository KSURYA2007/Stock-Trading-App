package cmp.rest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cmp.rest.model.enums.OrderSide;

public class Trade {

    private int tradeId;
    private int orderId;
    private int accountId;
    private int stockId;
    private OrderSide side;
    private int quantity;
    private BigDecimal executionPrice;
    private BigDecimal tradeValue;
    private LocalDateTime executedAt;

    public Trade(int tradeId, int orderId, int accountId,
                 int stockId, OrderSide side, int quantity,
                 BigDecimal executionPrice) {

        if (quantity <= 0 || executionPrice == null ||
            executionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid trade details");
        }

        this.tradeId = tradeId;
        this.orderId = orderId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.side = side;
        this.quantity = quantity;
        this.executionPrice = executionPrice;

        this.tradeValue = executionPrice.multiply(
                BigDecimal.valueOf(quantity));

        this.executedAt = LocalDateTime.now();
    }

    public int getTradeId() {
        return tradeId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getStockId() {
        return stockId;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public BigDecimal getTradeValue() {
        return tradeValue;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
}