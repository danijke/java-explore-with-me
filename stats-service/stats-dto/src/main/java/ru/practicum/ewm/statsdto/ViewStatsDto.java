package ru.practicum.ewm.statsdto;

import lombok.*;

@Value
@Builder
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;
}
