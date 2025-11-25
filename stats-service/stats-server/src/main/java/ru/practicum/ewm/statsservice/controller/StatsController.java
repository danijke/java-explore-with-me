package ru.practicum.ewm.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.statsdto.*;
import ru.practicum.ewm.statsservice.dto.DateTimeParser;
import ru.practicum.ewm.statsservice.service.StatsService;

import java.time.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody HitDto hitDto) {
        service.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        return service.getStats(DateTimeParser.parse(start), DateTimeParser.parse(end), uris, unique);
    }
}