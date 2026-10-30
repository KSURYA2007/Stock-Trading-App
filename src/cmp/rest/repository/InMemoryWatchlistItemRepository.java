package cmp.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryWatchlistItemRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements WatchlistItemRepository<T> {
    private final Function<T, Integer> watchlistIdExtractor;

    public InMemoryWatchlistItemRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> watchlistIdExtractor) {
        super(idExtractor);
        this.watchlistIdExtractor = Objects.requireNonNull(watchlistIdExtractor, "watchlistIdExtractor");
    }

    @Override
    public Optional<T> findByItemId(Integer itemId) {
        return findById(itemId);
    }

    @Override
    public List<T> findByWatchlistId(Integer watchlistId) {
        return findAllMatching(item -> Objects.equals(watchlistIdExtractor.apply(item), watchlistId));
    }
}
