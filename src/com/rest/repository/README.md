# Repository Layer

This package stores model objects in private, per-repository `HashMap` instances. It does not connect to a database and contains no business or CLI logic.

The Model Layer is not present in this project yet, so the repository classes are generic (`T`) rather than importing or duplicating model classes. The ER diagram uses integer IDs; repository contracts therefore use `Integer` IDs. Constructors receive method references from the final model classes, so their package and class names can be decided by the Model Layer without changing these repository APIs.

## CRUD behavior

- `save(entity)` inserts and returns the entity. It throws `IllegalArgumentException` when the ID is already stored, and rejects null entities or IDs.
- `findById(id)` returns `Optional.empty()` when no entity exists.
- `findAll()` returns a separate list; changing that list does not change repository storage.
- `update(entity)` replaces an existing entity with the same ID and returns `false` if that ID is absent.
- `deleteById(id)` returns `false` if that ID is absent.
- `existsById(id)` checks whether the ID is stored.

Each repository instance owns its own data. Create it once in the application composition/service setup and inject that same instance wherever it is needed; do not construct a new repository for every service call.

## Connecting the Model Layer

For example, once `User` has `getUserId()`, `getEmail()`, and `getPanNumber()` methods, create the repository like this:

```java
UserRepository<User> users = new InMemoryUserRepository<>(
        User::getUserId,
        User::getEmail,
        User::getPanNumber);
```

The same pattern applies to other repositories: pass the entity ID getter first, followed by getters for the fields used by that repository's lookup methods. For example, `InMemoryHoldingRepository` expects holding ID, account ID, and stock ID getters.

`findByEmail`, `findBySymbol`, and other single-result searches return the first match. The model or service layer should enforce uniqueness where required. `findActiveAlerts()` treats an alert as active when its ERD `is_triggered` field is false.

## Service Layer example

```java
UserRepository<User> users = new InMemoryUserRepository<>(
        User::getUserId, User::getEmail, User::getPanNumber);

users.save(user);
Optional<User> result = users.findByEmail("person@example.com");
```

The service should keep and reuse the repository instance. Validation, balance calculations, and order execution belong in the Service Layer, not here.
