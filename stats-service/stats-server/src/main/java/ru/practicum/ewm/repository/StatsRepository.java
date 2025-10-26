package ru.practicum.ewm.repository;

import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            select new ru.practicum.ewm.dto.ViewStatsDto(eph.app, eph.uri, count(eph))
            from EndpointHit eph
            where eph.timestamp between :start and :end
            and (:uris is null or eph.uri in (:uris))
            group by eph.app, eph.uri
            order by count(eph) desc
            """)
    List<ViewStatsDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );


    @Query("""
            select new ru.practicum.ewm.dto.ViewStatsDto(eph.app, eph.uri, count(distinct eph.ip))
            from EndpointHit eph
            where eph.timestamp between :start and :end
            and (:uris is null or eph.uri in (:uris))
            group by eph.app, eph.uri
            order by count(distinct eph.ip) desc
            """)
    List<ViewStatsDto> getStatsUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

}
