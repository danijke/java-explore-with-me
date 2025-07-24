package ru.practicum.ewm.statsservice.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParser {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, DTF);
    }
}