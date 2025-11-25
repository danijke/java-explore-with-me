package ru.practicum.ewm.statsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.statsservice.exception.BadRequestException;
import ru.practicum.ewm.statsservice.mapper.HitMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.statsservice.repository.HitRepository;
import ru.practicum.ewm.statsdto.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final HitRepository repository;

    @Transactional
    public void saveHit(HitDto hitDto) {
        repository.save(HitMapper.toHit(hitDto));
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("end must not be before start");
        }
        boolean hasUris = uris != null && !uris.isEmpty();
        if (unique) {
            return hasUris ? repository.findStatsUniqueByUris(start, end, uris)
                    : repository.findStatsUnique(start, end);
        } else {
            return hasUris ? repository.findStatsByUris(start, end, uris)
                    : repository.findStats(start, end);
        }
    }
}
