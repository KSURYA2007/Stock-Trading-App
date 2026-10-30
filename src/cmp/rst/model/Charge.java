package cmp.rst.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Charge {

    private int chargeId;
    private int tradeId;
    private BigDecimal brokerage;
    private BigDecimal gst;
    private BigDecimal totalCharge;

    public Charge(int chargeId, int tradeId, BigDecimal tradeValue) {

        if (tradeValue == null ||
            tradeValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid trade value");
        }

        this.chargeId = chargeId;
        this.tradeId = tradeId;

        // Brokerage = 0.05% of trade value, maximum ₹20
        BigDecimal calculatedBrokerage = tradeValue
                .multiply(new BigDecimal("0.0005"));

        this.brokerage = calculatedBrokerage
                .min(new BigDecimal("20.00"))
                .setScale(2, RoundingMode.HALF_UP);

        // GST = 18% of brokerage
        this.gst = brokerage
                .multiply(new BigDecimal("0.18"))
                .setScale(2, RoundingMode.HALF_UP);

        this.totalCharge = brokerage.add(gst);
    }

    public int getChargeId() {
        return chargeId;
    }

    public int getTradeId() {
        return tradeId;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }
}