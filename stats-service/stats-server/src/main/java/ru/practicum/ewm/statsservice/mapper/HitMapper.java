package ru.practicum.ewm.statsservice.mapper;

import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsservice.model.EndpointHit;

public class HitMapper {
    public static EndpointHit toHit(HitDto hitDto) {
        return EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
