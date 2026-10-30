package cmp.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryPriceAlertRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements PriceAlertRepository<T> {
    private final Function<T, Integer> userIdExtractor;
    private final Function<T, Integer> stockIdExtractor;
    private final Function<T, Boolean> triggeredExtractor;

    public InMemoryPriceAlertRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> userIdExtractor,
            Function<T, Integer> stockIdExtractor,
            Function<T, Boolean> triggeredExtractor) {
        super(idExtractor);
        this.userIdExtractor = Objects.requireNonNull(userIdExtractor, "userIdExtractor");
        this.stockIdExtractor = Objects.requireNonNull(stockIdExtractor, "stockIdExtractor");
        this.triggeredExtractor = Objects.requireNonNull(triggeredExtractor, "triggeredExtractor");
    }

    @Override
    public Optional<T> findByAlertId(Integer alertId) {
        return findById(alertId);
    }

    @Override
    public List<T> findByUserId(Integer userId) {
        return findAllMatching(alert -> Objects.equals(userIdExtractor.apply(alert), userId));
    }

    @Override
    public List<T> findByStockId(Integer stockId) {
        return findAllMatching(alert -> Objects.equals(stockIdExtractor.apply(alert), stockId));
    }

    @Override
    public List<T> findActiveAlerts() {
        return findAllMatching(alert -> !Boolean.TRUE.equals(triggeredExtractor.apply(alert)));
    }
}
