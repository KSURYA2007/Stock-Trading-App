package cmp.rst.model;

import java.math.BigDecimal;
import cmp.rst.model.enums.OrderSide;

public class LimitOrder extends Order {

    private BigDecimal limitPrice;

    public LimitOrder(int orderId, int accountId, int stockId,
                      OrderSide side, int quantity,
                      BigDecimal limitPrice) {

        super(orderId, accountId, stockId, side, quantity);

        if (limitPrice == null ||
            limitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid limit price");
        }

        this.limitPrice = limitPrice;
    }

    @Override
    public boolean canExecute(BigDecimal marketPrice) {

        if (marketPrice == null ||
            marketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (getSide() == OrderSide.BUY) {
            return marketPrice.compareTo(limitPrice) <= 0;
        }

        return marketPrice.compareTo(limitPrice) >= 0;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }
}