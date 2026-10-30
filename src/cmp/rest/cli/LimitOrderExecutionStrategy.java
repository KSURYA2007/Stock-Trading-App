package cmp.rest.cli;

public class LimitOrderExecutionStrategy implements OrderExecutionStrategy {
    @Override
    public boolean canExecute(Order order, Stock stock) {
        if (order.getOrderType() != OrderType.LIMIT || order.getLimitPrice() == null) {
            return false;
        }
        int comparison = stock.getCurrentPrice().compareTo(order.getLimitPrice());
        return order.getSide() == OrderSide.BUY ? comparison <= 0 : comparison >= 0;
    }
}
