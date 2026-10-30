package cmp.rest.stocktrading;

public interface OrderExecutionStrategy {
    boolean canExecute(Order order, Stock stock);
}
