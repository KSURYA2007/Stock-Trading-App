package cmp.rest.repository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByBankId(Integer bankId);

    List<T> findByUserId(Integer userId);
}
