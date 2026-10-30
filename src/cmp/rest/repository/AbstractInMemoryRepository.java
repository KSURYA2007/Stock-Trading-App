package cmp.rest.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractInMemoryRepository<T, ID> implements CrudRepository<T, ID> {
    private final Map<ID, T> entitiesById = new HashMap<>();
    private final Function<T, ID> idExtractor;

    protected AbstractInMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor");
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "entity");
        ID id = requireId(entity);
        if (entitiesById.containsKey(id)) {
            throw new IllegalArgumentException("An entity with ID " + id + " already exists");
        }
        entitiesById.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entitiesById.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entitiesById.values());
    }

    @Override
    public boolean update(T entity) {
        Objects.requireNonNull(entity, "entity");
        ID id = requireId(entity);
        if (!entitiesById.containsKey(id)) {
            return false;
        }
        entitiesById.put(id, entity);
        return true;
    }

    @Override
    public boolean deleteById(ID id) {
        return entitiesById.remove(id) != null;
    }

    @Override
    public boolean existsById(ID id) {
        return entitiesById.containsKey(id);
    }

    protected List<T> findAllMatching(Predicate<T> predicate) {
        List<T> matches = new ArrayList<>();
        for (T entity : entitiesById.values()) {
            if (predicate.test(entity)) {
                matches.add(entity);
            }
        }
        return matches;
    }

    protected Optional<T> findFirstMatching(Predicate<T> predicate) {
        for (T entity : entitiesById.values()) {
            if (predicate.test(entity)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    private ID requireId(T entity) {
        return Objects.requireNonNull(idExtractor.apply(entity), "entity ID");
    }
}
