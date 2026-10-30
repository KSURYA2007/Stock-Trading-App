package cmp.rest.repository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByWatchlistId(Integer watchlistId);

    List<T> findByUserId(Integer userId);
}
