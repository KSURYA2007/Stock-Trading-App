package cmp.rest.repository;

import java.util.Optional;

public interface UserRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByUserId(Integer userId);

    Optional<T> findByEmail(String email);

    Optional<T> findByPanNumber(String panNumber);
}
