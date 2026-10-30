package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class InMemoryStockPriceHistoryRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements StockPriceHistoryRepository<T> {
    private final Function<T, Integer> stockIdExtractor;

    public InMemoryStockPriceHistoryRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> stockIdExtractor) {
        super(idExtractor);
        this.stockIdExtractor = Objects.requireNonNull(stockIdExtractor, "stockIdExtractor");
    }

    @Override
    public List<T> findByStockId(Integer stockId) {
        return findAllMatching(history -> Objects.equals(stockIdExtractor.apply(history), stockId));
    }
}
