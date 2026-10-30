package cmp.rest.stocktrading;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IndianMarketHours implements MarketHoursPolicy {
    private static final LocalTime OPEN = LocalTime.of(9, 15);
    private static final LocalTime CLOSE = LocalTime.of(15, 30);
    @Override
    public boolean isOpen(LocalDateTime time) {
        DayOfWeek day = time.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY
                && !time.toLocalTime().isBefore(OPEN)
                && time.toLocalTime().isBefore(CLOSE);
    }
}
