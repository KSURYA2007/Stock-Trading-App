package cmp.rest.repository;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository<T> extends CrudRepository<T, Integer> {
    Optional<T> findByAlertId(Integer alertId);

    List<T> findByUserId(Integer userId);

    List<T> findByStockId(Integer stockId);

    List<T> findActiveAlerts();
}
