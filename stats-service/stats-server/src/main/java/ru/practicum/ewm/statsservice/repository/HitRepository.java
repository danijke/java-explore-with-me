package ru.practicum.ewm.statsservice.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.statsservice.model.EndpointHit;
import ru.practicum.ewm.statsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ewm.statsdto.ViewStatsDto(h.app, h.uri, count(h)) " +
            "from EndpointHit h where h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(h) desc")
    List<ViewStatsDto> findStats(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.ewm.statsdto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h where h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(distinct h.ip) desc")
    List<ViewStatsDto> findStatsUnique(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.ewm.statsdto.ViewStatsDto(h.app, h.uri, count(h)) " +
            "from EndpointHit h where h.timestamp between :start and :end and h.uri in :uris " +
            "group by h.app, h.uri order by count(h) desc")
    List<ViewStatsDto> findStatsByUris(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.statsdto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h where h.timestamp between :start and :end and h.uri in :uris " +
            "group by h.app, h.uri order by count(distinct h.ip) desc")
    List<ViewStatsDto> findStatsUniqueByUris(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris);
}