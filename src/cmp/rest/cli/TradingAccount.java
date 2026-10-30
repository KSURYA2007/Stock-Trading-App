package cmp.rest.cli;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradingAccount extends Account {
    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;
    private KycStatus kycStatus;
    private AccountStatus accountStatus;
    private Integer approvedBy;
    private final LocalDateTime createdAt;

    public TradingAccount(int accountId, int userId, LocalDateTime createdAt) {
        super(accountId, userId);
        availableBalance = BigDecimal.ZERO.setScale(2);
        blockedBalance = BigDecimal.ZERO.setScale(2);
        kycStatus = KycStatus.PENDING;
        accountStatus = AccountStatus.ACTIVE;
        this.createdAt = createdAt;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getBlockedBalance() {
        return blockedBalance;
    }

    public void setBlockedBalance(BigDecimal blockedBalance) {
        this.blockedBalance = blockedBalance;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getAccountType() {
        return "Trading";
    }
}
