package ru.practicum.ewm.event.service;

import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.HitDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class EventService {
    private final StatsClient client;
    public void getEvents(HttpServletRequest request) {
        sendHit(request);
    }

    public void getEventById(Long id, HttpServletRequest request) {
        sendHit(request);
    }
    private void sendHit(HttpServletRequest request) {
        HitDto dto = HitDto.builder()
                .app("ewm-main-ru.practicum.ewm.service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        client.sendHit(dto);
    }
}
