package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryStockRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements StockRepository<T> {
    private final Function<T, String> symbolExtractor;
    private final Function<T, String> sectorExtractor;
    private final Function<T, String> statusExtractor;

    public InMemoryStockRepository(
            Function<T, Integer> idExtractor,
            Function<T, String> symbolExtractor,
            Function<T, String> sectorExtractor,
            Function<T, String> statusExtractor) {
        super(idExtractor);
        this.symbolExtractor = Objects.requireNonNull(symbolExtractor, "symbolExtractor");
        this.sectorExtractor = Objects.requireNonNull(sectorExtractor, "sectorExtractor");
        this.statusExtractor = Objects.requireNonNull(statusExtractor, "statusExtractor");
    }

    @Override
    public Optional<T> findByStockId(Integer stockId) {
        return findById(stockId);
    }

    @Override
    public Optional<T> findBySymbol(String symbol) {
        return findFirstMatching(stock -> Objects.equals(symbolExtractor.apply(stock), symbol));
    }

    @Override
    public List<T> findBySector(String sector) {
        return findAllMatching(stock -> Objects.equals(sectorExtractor.apply(stock), sector));
    }

    @Override
    public List<T> findByStatus(String status) {
        return findAllMatching(stock -> Objects.equals(statusExtractor.apply(stock), status));
    }
}
