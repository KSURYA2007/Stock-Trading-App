package com.rest.repository;

import java.util.List;

public interface StockPriceHistoryRepository<T> extends CrudRepository<T, Integer> {
    List<T> findByStockId(Integer stockId);
}
