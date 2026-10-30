package cmp.rest.stocktrading;

import java.math.BigDecimal;

public class Charge {
    private final int chargeId;
    private final int tradeId;
    private final BigDecimal brokerage;
    private final BigDecimal gst;
    private final BigDecimal totalCharge;

    public Charge(int chargeId, int tradeId, BigDecimal brokerage, BigDecimal gst, BigDecimal totalCharge) {
        this.chargeId = chargeId;
        this.tradeId = tradeId;
        this.brokerage = brokerage;
        this.gst = gst;
        this.totalCharge = totalCharge;
    }

    public int getChargeId() { return chargeId; }
    public int getTradeId() { return tradeId; }
    public BigDecimal getBrokerage() { return brokerage; }
    public BigDecimal getGst() { return gst; }
    public BigDecimal getTotalCharge() { return totalCharge; }
}
