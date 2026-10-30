package stocktrading;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    private final int tradeId;
    private final int orderId;
    private final BigDecimal executionPrice;
    private final int quantity;
    private final BigDecimal tradeValue;
    private final LocalDateTime executedAt;

    public Trade(int tradeId, int orderId, BigDecimal executionPrice, int quantity,
                 BigDecimal tradeValue, LocalDateTime executedAt) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.executionPrice = executionPrice;
        this.quantity = quantity;
        this.tradeValue = tradeValue;
        this.executedAt = executedAt;
    }

    public int getTradeId() { return tradeId; }
    public int getOrderId() { return orderId; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTradeValue() { return tradeValue; }
    public LocalDateTime getExecutedAt() { return executedAt; }
}
