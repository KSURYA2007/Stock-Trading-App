package com.rest.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryChargeRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements ChargeRepository<T> {
    private final Function<T, Integer> tradeIdExtractor;

    public InMemoryChargeRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> tradeIdExtractor) {
        super(idExtractor);
        this.tradeIdExtractor = Objects.requireNonNull(tradeIdExtractor, "tradeIdExtractor");
    }

    @Override
    public Optional<T> findByChargeId(Integer chargeId) {
        return findById(chargeId);
    }

    @Override
    public Optional<T> findByTradeId(Integer tradeId) {
        return findFirstMatching(charge -> Objects.equals(tradeIdExtractor.apply(charge), tradeId));
    }
}
