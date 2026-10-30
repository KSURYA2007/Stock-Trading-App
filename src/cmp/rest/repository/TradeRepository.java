package cmp.rest.repository;

import java.util.Optional;

public interface TradeRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByTradeId(Integer tradeId);

    Optional<T> findByOrderId(Integer orderId);
}
