package cmp.rest.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryUserRepository<T> extends AbstractInMemoryRepository<T, Integer>
        implements UserRepository<T> {
    private final Function<T, String> emailExtractor;
    private final Function<T, String> panNumberExtractor;

    public InMemoryUserRepository(
            Function<T, Integer> idExtractor,
            Function<T, String> emailExtractor,
            Function<T, String> panNumberExtractor) {
        super(idExtractor);
        this.emailExtractor = Objects.requireNonNull(emailExtractor, "emailExtractor");
        this.panNumberExtractor = Objects.requireNonNull(panNumberExtractor, "panNumberExtractor");
    }

    @Override
    public Optional<T> findByUserId(Integer userId) {
        return findById(userId);
    }

    @Override
    public Optional<T> findByEmail(String email) {
        return findFirstMatching(user -> Objects.equals(emailExtractor.apply(user), email));
    }

    @Override
    public Optional<T> findByPanNumber(String panNumber) {
        return findFirstMatching(user -> Objects.equals(panNumberExtractor.apply(user), panNumber));
    }
}
