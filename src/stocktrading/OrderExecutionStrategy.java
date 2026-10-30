package stocktrading;

public interface OrderExecutionStrategy {
    boolean canExecute(Order order, Stock stock);
}
