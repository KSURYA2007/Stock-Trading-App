package cmp.rest.cli;

public interface OrderExecutionStrategy {
    boolean canExecute(Order order, Stock stock);
}
