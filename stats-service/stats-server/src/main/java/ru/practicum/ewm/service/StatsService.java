package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.mapper.StatsMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.StatsRepository;
import ru.practicum.ewm.dto.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository repository;
    public void saveHit(HitDto hitDto) {
        repository.save(StatsMapper.toHit(hitDto));
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return unique ? repository.getStatsUniqueIp(start, end, uris) : repository.getStats(start, end, uris);
    }
}
