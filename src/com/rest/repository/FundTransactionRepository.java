package com.rest.repository;

import java.util.List;
import java.util.Optional;

public interface FundTransactionRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByTransactionId(Integer transactionId);

    List<T> findByAccountId(Integer accountId);
}
