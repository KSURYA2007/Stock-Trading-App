package cmp.rest.repository;

import java.util.List;

public interface TradingAccountRepository<T> extends CrudRepository<T, Integer> {
    java.util.Optional<T> findByAccountId(Integer accountId);

    List<T> findByUserId(Integer userId);

    List<T> findByApprovedBy(Integer approvedBy);
}
