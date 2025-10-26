package ru.practicum.ewm.dto;

import lombok.*;

public record ViewStatsDto(String app, String uri, long hits) {
}
