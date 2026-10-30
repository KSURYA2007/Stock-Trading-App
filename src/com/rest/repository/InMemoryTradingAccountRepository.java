package com.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class InMemoryTradingAccountRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements TradingAccountRepository<T> {
    private final Function<T, Integer> userIdExtractor;
    private final Function<T, Integer> approvedByExtractor;

    public InMemoryTradingAccountRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> userIdExtractor,
            Function<T, Integer> approvedByExtractor) {
        super(idExtractor);
        this.userIdExtractor = Objects.requireNonNull(userIdExtractor, "userIdExtractor");
        this.approvedByExtractor = Objects.requireNonNull(approvedByExtractor, "approvedByExtractor");
    }

    @Override
    public java.util.Optional<T> findByAccountId(Integer accountId) {
        return findById(accountId);
    }

    @Override
    public List<T> findByUserId(Integer userId) {
        return findAllMatching(account -> Objects.equals(userIdExtractor.apply(account), userId));
    }

    @Override
    public List<T> findByApprovedBy(Integer approvedBy) {
        return findAllMatching(account -> Objects.equals(approvedByExtractor.apply(account), approvedBy));
    }
}
