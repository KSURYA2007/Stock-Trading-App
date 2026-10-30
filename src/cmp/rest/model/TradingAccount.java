package cmp.rest.model;

import java.math.BigDecimal;

import cmp.rest.model.enums.AccountStatus;
import cmp.rest.model.enums.KycStatus;

public class TradingAccount {

    private int accountId;
    private int userId;

    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;

    private KycStatus kycStatus;
    private AccountStatus accountStatus;

    private int approvedBy;

    public TradingAccount(int accountId, int userId) {

        this.accountId = accountId;
        this.userId = userId;

        this.availableBalance = BigDecimal.ZERO;
        this.blockedBalance = BigDecimal.ZERO;

        this.kycStatus = KycStatus.PENDING;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getBlockedBalance() {
        return blockedBalance;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void addAvailableBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        availableBalance = availableBalance.add(amount);
    }

    public void blockFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 ||
            availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        availableBalance = availableBalance.subtract(amount);
        blockedBalance = blockedBalance.add(amount);
    }

    public void releaseFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 ||
            blockedBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Invalid blocked amount");
        }

        blockedBalance = blockedBalance.subtract(amount);
        availableBalance = availableBalance.add(amount);
    }
}