package stocktrading;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private final int orderId;
    private final int accountId;
    private final int stockId;
    private final OrderSide side;
    private final OrderType orderType;
    private final int quantity;
    private final BigDecimal limitPrice;
    private BigDecimal executionPrice;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public Order(int orderId, int accountId, int stockId, OrderSide side, OrderType orderType,
                 int quantity, BigDecimal limitPrice, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.side = side;
        this.orderType = orderType;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = OrderStatus.PENDING;
    }

    public int getOrderId() { return orderId; }
    public int getAccountId() { return accountId; }
    public int getStockId() { return stockId; }
    public OrderSide getSide() { return side; }
    public OrderType getOrderType() { return orderType; }
    public int getQuantity() { return quantity; }
    public BigDecimal getLimitPrice() { return limitPrice; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public void setExecutionPrice(BigDecimal executionPrice) { this.executionPrice = executionPrice; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
