package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryBankAccountRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements BankAccountRepository<T> {
    private final Function<T, Integer> userIdExtractor;

    public InMemoryBankAccountRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> userIdExtractor) {
        super(idExtractor);
        this.userIdExtractor = Objects.requireNonNull(userIdExtractor, "userIdExtractor");
    }

    @Override
    public Optional<T> findByBankId(Integer bankId) {
        return findById(bankId);
    }

    @Override
    public List<T> findByUserId(Integer userId) {
        return findAllMatching(account -> Objects.equals(userIdExtractor.apply(account), userId));
    }
}
