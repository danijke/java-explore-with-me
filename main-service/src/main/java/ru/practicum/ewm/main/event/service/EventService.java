package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.commons.enums.*;
import ru.practicum.ewm.main.event.dto.*;

import java.util.List;

public interface EventService {

    List<EventShortDto> searchPublic(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     String rangeStart,
                                     String rangeEnd,
                                     Boolean onlyAvailable,
                                     PublicSort sort,
                                     Integer from,
                                     Integer size);

    EventFullDto getPublishedById(Long id);

    List<EventFullDto> searchAdmin(List<Long> users,
                                   List<EventState> states,
                                   List<Long> categories,
                                   String rangeStart,
                                   String rangeEnd,
                                   Integer from,
                                   Integer size);

    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> findOwnEvents(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto dto);

    EventFullDto getOwnEvent(Long userId, Long eventId);

    EventFullDto updateOwnEvent(Long userId, Long eventId, UpdateEventUserRequest request);
}
