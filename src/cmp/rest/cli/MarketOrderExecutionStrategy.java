package cmp.rest.cli;

public class MarketOrderExecutionStrategy implements OrderExecutionStrategy {
    @Override
    public boolean canExecute(Order order, Stock stock) {
        return order.getOrderType() == OrderType.MARKET;
    }
}
