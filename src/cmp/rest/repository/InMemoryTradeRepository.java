package cmp.rest.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryTradeRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements TradeRepository<T> {
    private final Function<T, Integer> orderIdExtractor;

    public InMemoryTradeRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> orderIdExtractor) {
        super(idExtractor);
        this.orderIdExtractor = Objects.requireNonNull(orderIdExtractor, "orderIdExtractor");
    }

    @Override
    public Optional<T> findByTradeId(Integer tradeId) {
        return findById(tradeId);
    }

    @Override
    public Optional<T> findByOrderId(Integer orderId) {
        return findFirstMatching(trade -> Objects.equals(orderIdExtractor.apply(trade), orderId));
    }
}
