package com.rest.repository;

import java.util.List;
import java.util.Optional;

public interface StockRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByStockId(Integer stockId);

    Optional<T> findBySymbol(String symbol);

    List<T> findBySector(String sector);

    List<T> findByStatus(String status);
}
