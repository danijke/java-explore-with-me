package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.model.EndpointHit;

public class StatsMapper {
    public static EndpointHit toHit(HitDto hitDto) {
        return EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
