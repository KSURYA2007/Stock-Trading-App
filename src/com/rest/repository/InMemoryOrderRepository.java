package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryOrderRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements OrderRepository<T> {
    private final Function<T, Integer> accountIdExtractor;
    private final Function<T, Integer> stockIdExtractor;
    private final Function<T, String> statusExtractor;

    public InMemoryOrderRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> accountIdExtractor,
            Function<T, Integer> stockIdExtractor,
            Function<T, String> statusExtractor) {
        super(idExtractor);
        this.accountIdExtractor = Objects.requireNonNull(accountIdExtractor, "accountIdExtractor");
        this.stockIdExtractor = Objects.requireNonNull(stockIdExtractor, "stockIdExtractor");
        this.statusExtractor = Objects.requireNonNull(statusExtractor, "statusExtractor");
    }

    @Override
    public Optional<T> findByOrderId(Integer orderId) {
        return findById(orderId);
    }

    @Override
    public List<T> findByAccountId(Integer accountId) {
        return findAllMatching(order -> Objects.equals(accountIdExtractor.apply(order), accountId));
    }

    @Override
    public List<T> findByStockId(Integer stockId) {
        return findAllMatching(order -> Objects.equals(stockIdExtractor.apply(order), stockId));
    }

    @Override
    public List<T> findByStatus(String status) {
        return findAllMatching(order -> Objects.equals(statusExtractor.apply(order), status));
    }
}
