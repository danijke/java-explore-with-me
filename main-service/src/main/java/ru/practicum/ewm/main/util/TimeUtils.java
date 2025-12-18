package ru.practicum.ewm.main.util;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static LocalDateTime parseOrNull(@Nullable String value, DateTimeFormatter formatter) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value, formatter);
    }

    public static String format(LocalDateTime ldt, DateTimeFormatter formatter) {
        return ldt == null ? null : ldt.format(formatter);
    }
}
