package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryWatchlistRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements WatchlistRepository<T> {
    private final Function<T, Integer> userIdExtractor;

    public InMemoryWatchlistRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> userIdExtractor) {
        super(idExtractor);
        this.userIdExtractor = Objects.requireNonNull(userIdExtractor, "userIdExtractor");
    }

    @Override
    public Optional<T> findByWatchlistId(Integer watchlistId) {
        return findById(watchlistId);
    }

    @Override
    public List<T> findByUserId(Integer userId) {
        return findAllMatching(watchlist -> Objects.equals(userIdExtractor.apply(watchlist), userId));
    }
}
