package cmp.rest.model;

import java.math.BigDecimal;

import cmp.rest.model.enums.OrderSide;

public class MarketOrder extends Order {

    public MarketOrder(int orderId, int accountId, int stockId,
                       OrderSide side, int quantity) {

        super(orderId, accountId, stockId, side, quantity);
    }

    @Override
    public boolean canExecute(BigDecimal marketPrice) {

        return marketPrice != null
                && marketPrice.compareTo(BigDecimal.ZERO) > 0;
    }
}