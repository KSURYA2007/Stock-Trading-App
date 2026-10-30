package cmp.rest.stocktrading;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundTransaction {
    private final int transactionId;
    private final int accountId;
    private final FundTransactionType type;
    private final BigDecimal amount;
    private final String transactionMode;
    private FundTransactionStatus status;
    private final LocalDateTime createdAt;

    public FundTransaction(int transactionId, int accountId, FundTransactionType type,
                           BigDecimal amount, String transactionMode, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.transactionMode = transactionMode;
        this.createdAt = createdAt;
        this.status = FundTransactionStatus.COMPLETED;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public FundTransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public FundTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(FundTransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
