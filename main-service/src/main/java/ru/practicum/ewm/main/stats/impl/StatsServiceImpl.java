package ru.practicum.ewm.main.stats.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.config.AppProperties;
import ru.practicum.ewm.main.stats.StatsService;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.statsdto.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsClient statsClient;
    private final AppProperties appProperties;

    @Override
    public void recordHit(HttpServletRequest request) {
        HitDto hit = HitDto.builder()
                .app(appProperties.getName())
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.saveHit(hit);
    }

    @Override
    public Map<String, Long> getViews(List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return new HashMap<>();
        }
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

        Map<String, Long> map = new HashMap<>();
        for (ViewStatsDto s : stats) {
            map.put(s.getUri(), s.getHits() == null ? 0L : s.getHits());
        }
        return map;
    }
}
