package cmp.rst.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceAlert {

    private int alertId;
    private int accountId;
    private int stockId;
    private BigDecimal targetPrice;
    private boolean triggered;
    private LocalDateTime createdAt;

    public PriceAlert(int alertId, int accountId, int stockId,
                      BigDecimal targetPrice) {

        if (targetPrice == null ||
            targetPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid target price");
        }

        this.alertId = alertId;
        this.accountId = accountId;
        this.stockId = stockId;
        this.targetPrice = targetPrice;
        this.triggered = false;
        this.createdAt = LocalDateTime.now();
    }

    public void triggerAlert() {
        this.triggered = true;
    }

    public int getAlertId() {
        return alertId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getStockId() {
        return stockId;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}