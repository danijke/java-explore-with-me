package ru.practicum.ewm.main.event.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.commons.enums.EventState;
import ru.practicum.ewm.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.*;

public interface EventRepositoryCustom {

    List<Event> searchPublic(String text,
                             List<Long> categories,
                             Boolean paid,
                             LocalDateTime rangeStart,
                             LocalDateTime rangeEnd,
                             EventState state,
                             Pageable pageable);

    List<Event> searchAdmin(List<Long> users,
                            List<EventState> states,
                            List<Long> categories,
                            LocalDateTime rangeStart,
                            LocalDateTime rangeEnd,
                            Pageable pageable);

    List<Event> findByIds(Set<Long> ids);
}
