package cmp.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryHoldingRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements HoldingRepository<T> {
    private final Function<T, Integer> accountIdExtractor;
    private final Function<T, Integer> stockIdExtractor;

    public InMemoryHoldingRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> accountIdExtractor,
            Function<T, Integer> stockIdExtractor) {
        super(idExtractor);
        this.accountIdExtractor = Objects.requireNonNull(accountIdExtractor, "accountIdExtractor");
        this.stockIdExtractor = Objects.requireNonNull(stockIdExtractor, "stockIdExtractor");
    }

    @Override
    public Optional<T> findByHoldingId(Integer holdingId) {
        return findById(holdingId);
    }

    @Override
    public List<T> findByAccountId(Integer accountId) {
        return findAllMatching(holding -> Objects.equals(accountIdExtractor.apply(holding), accountId));
    }

    @Override
    public List<T> findByStockId(Integer stockId) {
        return findAllMatching(holding -> Objects.equals(stockIdExtractor.apply(holding), stockId));
    }

    @Override
    public Optional<T> findByAccountIdAndStockId(Integer accountId, Integer stockId) {
        return findFirstMatching(holding ->
                Objects.equals(accountIdExtractor.apply(holding), accountId)
                        && Objects.equals(stockIdExtractor.apply(holding), stockId));
    }
}
