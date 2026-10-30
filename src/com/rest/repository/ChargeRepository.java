package com.rest.repository;

import java.util.Optional;

public interface ChargeRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByChargeId(Integer chargeId);

    Optional<T> findByTradeId(Integer tradeId);
}
