package cmp.rest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cmp.rest.model.enums.OrderSide;
import cmp.rest.model.enums.OrderStatus;

public abstract class Order {

    private int orderId;
    private int accountId;
    private int stockId;

    private OrderSide side;
    private int quantity;

    private BigDecimal executionPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime executedAt;

    public Order(int orderId, int accountId,
                 int stockId, OrderSide side,
                 int quantity) {

        if (quantity < 1 || quantity > 1000) {
            throw new IllegalArgumentException(
                "Quantity must be between 1 and 1000"
            );
        }

        this.orderId = orderId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.side = side;
        this.quantity = quantity;

        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Abstraction
    public abstract boolean canExecute(BigDecimal marketPrice);

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

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}