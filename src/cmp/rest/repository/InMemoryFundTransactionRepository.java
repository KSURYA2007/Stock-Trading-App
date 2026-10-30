package cmp.rest.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryFundTransactionRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements FundTransactionRepository<T> {
    private final Function<T, Integer> accountIdExtractor;

    public InMemoryFundTransactionRepository(
            Function<T, Integer> idExtractor,
            Function<T, Integer> accountIdExtractor) {
        super(idExtractor);
        this.accountIdExtractor = Objects.requireNonNull(accountIdExtractor, "accountIdExtractor");
    }

    @Override
    public Optional<T> findByTransactionId(Integer transactionId) {
        return findById(transactionId);
    }

    @Override
    public List<T> findByAccountId(Integer accountId) {
        return findAllMatching(transaction -> Objects.equals(accountIdExtractor.apply(transaction), accountId));
    }
}
