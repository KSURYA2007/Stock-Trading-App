package com.rest.repository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByHoldingId(Integer holdingId);

    List<T> findByAccountId(Integer accountId);

    List<T> findByStockId(Integer stockId);

    Optional<T> findByAccountIdAndStockId(Integer accountId, Integer stockId);
}
