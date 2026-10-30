package cmp.rest.stocktrading;

import java.time.LocalDateTime;

public interface MarketHoursPolicy {
    boolean isOpen(LocalDateTime time);
}
