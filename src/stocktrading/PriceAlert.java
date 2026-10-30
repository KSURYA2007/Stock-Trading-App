package stocktrading;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceAlert {
    private final int alertId;
    private final int userId;
    private final int stockId;
    private final BigDecimal targetPrice;
    private final AlertDirection direction;
    private boolean triggered;
    private final LocalDateTime createdAt;
    private LocalDateTime triggeredAt;

    public PriceAlert(int alertId, int userId, int stockId, BigDecimal targetPrice,
                      AlertDirection direction, LocalDateTime createdAt) {
        this.alertId = alertId;
        this.userId = userId;
        this.stockId = stockId;
        this.targetPrice = targetPrice;
        this.direction = direction;
        this.createdAt = createdAt;
        this.triggered = false;
    }

    public int getAlertId() { return alertId; }
    public int getUserId() { return userId; }
    public int getStockId() { return stockId; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public AlertDirection getDirection() { return direction; }
    public boolean isTriggered() { return triggered; }
    public void setTriggered(boolean triggered) { this.triggered = triggered; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
}
