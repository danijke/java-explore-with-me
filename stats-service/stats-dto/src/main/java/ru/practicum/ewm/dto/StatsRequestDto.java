package ru.practicum.ewm.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Value
@Builder
public class StatsRequestDto {
    LocalDateTime start;
    LocalDateTime end;
    ArrayList<String> uris;
    boolean unique;
}
