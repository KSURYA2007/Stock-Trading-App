package cmp.rest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cmp.rest.model.enums.TransactionStatus;
import cmp.rest.model.enums.TransactionType;

public class FundTransaction {

    private int transactionId;
    private int accountId;
    private int bankId;

    private BigDecimal amount;

    private TransactionType transactionType;
    private TransactionStatus status;

    private LocalDateTime createdAt;

    public FundTransaction(int transactionId,
                           int accountId,
                           int bankId,
                           BigDecimal amount,
                           TransactionType transactionType) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.transactionId = transactionId;
        this.accountId = accountId;
        this.bankId = bankId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getBankId() {
        return bankId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}