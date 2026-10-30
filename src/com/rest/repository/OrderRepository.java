package com.rest.repository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByOrderId(Integer orderId);

    List<T> findByAccountId(Integer accountId);

    List<T> findByStockId(Integer stockId);

    List<T> findByStatus(String status);
}
