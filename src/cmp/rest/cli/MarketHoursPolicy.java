package cmp.rest.cli;

import java.time.LocalDateTime;

public interface MarketHoursPolicy {
    boolean isOpen(LocalDateTime time);
}
