package ru.practicum.ewm.main.stats;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

public interface StatsService {

    void recordHit(HttpServletRequest request);

    Map<String, Long> getViews(List<String> uris);
}
