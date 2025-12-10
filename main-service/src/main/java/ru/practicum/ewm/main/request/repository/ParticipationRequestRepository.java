package ru.practicum.ewm.main.request.repository;

import org.springframework.data.jpa.repository.*;
import ru.practicum.main.commons.enums.RequestStatus;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.*;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("""
            SELECT pr.event.id, COUNT(pr)
            FROM ParticipationRequest pr
            WHERE pr.status = :status
            AND pr.event.id IN :eventIds
            GROUP BY pr.event.id
            """)
    List<Object[]> countConfirmedByEventIds(Set<Long> eventIds, ru.practicum.main.commons.enums.RequestStatus status);


    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByIdIn(Collection<Long> ids);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);
}
