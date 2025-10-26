package ru.practicum.ewm.dto;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
public class HitDto {
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
