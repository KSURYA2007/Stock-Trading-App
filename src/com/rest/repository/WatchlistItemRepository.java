package com.rest.repository;

import java.util.List;
import java.util.Optional;

public interface WatchlistItemRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByItemId(Integer itemId);

    List<T> findByWatchlistId(Integer watchlistId);
}
