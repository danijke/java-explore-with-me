package ru.practicum.ewm.main.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.mapper.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.commons.enums.*;
import ru.practicum.ewm.main.commons.model.LocationEmbeddable;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.mapper.EventMapper;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.main.stats.StatsService;
import ru.practicum.ewm.main.user.dto.UserShortDto;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;
import ru.practicum.ewm.main.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsService statsService;
    private final DateTimeFormatter apiDateTimeFormatter;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> searchPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            String rangeStart,
                                            String rangeEnd,
                                            Boolean onlyAvailable,
                                            PublicSort sort,
                                            Integer from,
                                            Integer size) {

        LocalDateTime start = TimeUtils.parseOrNull(rangeStart, apiDateTimeFormatter);
        LocalDateTime end   = TimeUtils.parseOrNull(rangeEnd,   apiDateTimeFormatter);

        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }
        if (start == null && end == null) {
            start = LocalDateTime.now();
        }

        List<Event> events = eventRepository.searchPublic(
                text, categories, paid, start, end, EventState.PUBLISHED, PageUtils.offset(from, size)
        );

        Map<Long, Long> confirmedMap = loadConfirmedCounts(events);
        Map<Long, Long> viewsMap = loadViewsFor(events);

        List<Event> filtered = new ArrayList<>(events);
        if (Boolean.TRUE.equals(onlyAvailable)) {
            filtered = filtered.stream()
                    .filter(e -> {
                        int limit = e.getParticipantLimit() == null ? 0 : e.getParticipantLimit();
                        if (limit == 0) return true;
                        Long confirmed = confirmedMap.getOrDefault(e.getId(), 0L);
                        return confirmed < limit;
                    })
                    .collect(Collectors.toList());
        }

        List<EventShortDto> dtos = toShortDtos(filtered, confirmedMap, viewsMap);

        if (sort == PublicSort.VIEWS) {
            dtos.sort(Comparator.comparingLong(EventShortDto::getViews).reversed());
        } else {
            dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublishedById(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        Map<Long, Long> confirmedMap = loadConfirmedCounts(Collections.singletonList(event));
        Map<Long, Long> viewsMap = loadViewsFor(Collections.singletonList(event));

        CategoryDto catDto = CategoryMapper.toDto(event.getCategory());
        UserShortDto userShort = UserMapper.toShortDto(event.getInitiator());

        return EventMapper.toFullDto(
                event, catDto, userShort, apiDateTimeFormatter,
                confirmedMap.get(event.getId()), viewsMap.get(event.getId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchAdmin(List<Long> users,
                                          List<EventState> states,
                                          List<Long> categories,
                                          String rangeStart,
                                          String rangeEnd,
                                          Integer from,
                                          Integer size) {

        LocalDateTime start = TimeUtils.parseOrNull(rangeStart, apiDateTimeFormatter);
        LocalDateTime end = TimeUtils.parseOrNull(rangeEnd, apiDateTimeFormatter);

        List<Event> events = eventRepository.searchAdmin(
                users, states, categories, start, end, PageUtils.offset(from, size)
        );

        Map<Long, Long> confirmedMap = loadConfirmedCounts(events);
        Map<Long, Long> viewsMap = loadViewsFor(events);

        return events.stream()
                .map(e -> EventMapper.toFullDto(
                        e,
                        CategoryMapper.toDto(e.getCategory()),
                        UserMapper.toShortDto(e.getInitiator()),
                        apiDateTimeFormatter,
                        confirmedMap.get(e.getId()),
                        viewsMap.get(e.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        applyAdminUpdate(event, request);

        Event saved = eventRepository.save(event);
        Map<Long, Long> confirmedMap = loadConfirmedCounts(Collections.singletonList(saved));
        Map<Long, Long> viewsMap = loadViewsFor(Collections.singletonList(saved));

        return EventMapper.toFullDto(
                saved,
                CategoryMapper.toDto(saved.getCategory()),
                UserMapper.toShortDto(saved.getInitiator()),
                apiDateTimeFormatter,
                confirmedMap.get(saved.getId()),
                viewsMap.get(saved.getId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findOwnEvents(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageUtils.offset(from, size));
        Map<Long, Long> confirmedMap = loadConfirmedCounts(events);
        Map<Long, Long> viewsMap = loadViewsFor(events);
        return toShortDtos(events, confirmedMap, viewsMap);
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto dto) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime eventDate = TimeUtils.parseOrNull(dto.getEventDate(), apiDateTimeFormatter);
        if (eventDate == null || !eventDate.isAfter(now.plusHours(2))) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + dto.getEventDate());
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException(
                    "Field: participantLimit. Error: must be greater than or equal to 0. Value: " + dto.getParticipantLimit()
            );
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        Event toSave = EventMapper.toEntity(dto, category, initiator, apiDateTimeFormatter, now);
        Event saved = eventRepository.save(toSave);

        return EventMapper.toFullDto(
                saved,
                CategoryMapper.toDto(saved.getCategory()),
                UserMapper.toShortDto(saved.getInitiator()),
                apiDateTimeFormatter,
                0L,
                0L
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOwnEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        Map<Long, Long> confirmedMap = loadConfirmedCounts(Collections.singletonList(event));
        Map<Long, Long> viewsMap = loadViewsFor(Collections.singletonList(event));
        return EventMapper.toFullDto(
                event,
                CategoryMapper.toDto(event.getCategory()),
                UserMapper.toShortDto(event.getInitiator()),
                apiDateTimeFormatter,
                confirmedMap.get(event.getId()),
                viewsMap.get(event.getId())
        );
    }

    @Override
    public EventFullDto updateOwnEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (!(event.getState() == EventState.PENDING || event.getState() == EventState.CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null) {
            LocalDateTime newDate = TimeUtils.parseOrNull(request.getEventDate(), apiDateTimeFormatter);
            if (newDate == null || !newDate.isAfter(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + request.getEventDate());
            }
            event.setEventDate(newDate);
        }

        applyCommonUpdate(event,
                request.getAnnotation(),
                request.getDescription(),
                request.getTitle(),
                request.getCategory(),
                request.getPaid(),
                request.getParticipantLimit(),
                request.getRequestModeration(),
                request.getLocation()
        );

        StateActionUser action = request.getStateAction();
        if (action != null) {
            if (action == StateActionUser.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else if (action == StateActionUser.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        Event saved = eventRepository.save(event);
        Map<Long, Long> confirmedMap = loadConfirmedCounts(Collections.singletonList(saved));
        Map<Long, Long> viewsMap = loadViewsFor(Collections.singletonList(saved));

        return EventMapper.toFullDto(
                saved,
                CategoryMapper.toDto(saved.getCategory()),
                UserMapper.toShortDto(saved.getInitiator()),
                apiDateTimeFormatter,
                confirmedMap.get(saved.getId()),
                viewsMap.get(saved.getId())
        );
    }

    private List<EventShortDto> toShortDtos(List<Event> events,
                                            Map<Long, Long> confirmedMap,
                                            Map<Long, Long> viewsMap) {
        return events.stream()
                .map(e -> EventMapper.toShortDto(
                        e,
                        CategoryMapper.toDto(e.getCategory()),
                        UserMapper.toShortDto(e.getInitiator()),
                        apiDateTimeFormatter,
                        confirmedMap.get(e.getId()),
                        viewsMap.get(e.getId())
                ))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> loadConfirmedCounts(List<Event> events) {
        if (events == null || events.isEmpty()) return Collections.emptyMap();
        Set<Long> ids = events.stream().map(Event::getId).collect(Collectors.toSet());
        List<Object[]> rows = requestRepository.countConfirmedByEventIds(ids, RequestStatus.CONFIRMED);
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            Long eventId = (Long) row[0];
            Long cnt = (Long) row[1];
            map.put(eventId, cnt == null ? 0L : cnt);
        }
        return map;
    }

    private Map<Long, Long> loadViewsFor(List<Event> events) {
        if (events == null || events.isEmpty()) return Collections.emptyMap();
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).toList();
        Map<String, Long> byUri = statsService.getViews(uris);
        Map<Long, Long> result = new HashMap<>();
        for (Event e : events) {
            Long v = byUri.getOrDefault("/events/" + e.getId(), 0L);
            result.put(e.getId(), v);
        }
        return result;
    }

    // import ru.practicum.main.exception.BadRequestException;
// import java.time.LocalDateTime;

    private void applyAdminUpdate(Event event, UpdateEventAdminRequest request) {
        if (request.getEventDate() != null) {
            LocalDateTime newDate = TimeUtils.parseOrNull(request.getEventDate(), apiDateTimeFormatter);
            // Валидация: дата должна быть в будущем
            if (newDate == null || !newDate.isAfter(LocalDateTime.now())) {
                throw new BadRequestException(
                        "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + request.getEventDate()
                );
            }
            event.setEventDate(newDate);
        }

        applyCommonUpdate(event, request.getAnnotation(), request.getDescription(), request.getTitle(),
                request.getCategory(), request.getPaid(), request.getParticipantLimit(),
                request.getRequestModeration(), request.getLocation());

        StateActionAdmin action = request.getStateAction();
        if (action != null) {
            if (action == StateActionAdmin.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
                }
                LocalDateTime publishTime = LocalDateTime.now();
                if (!event.getEventDate().isAfter(publishTime.plusHours(1))) {
                    throw new ConflictException("The event date must be at least an hour after the publication time");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(publishTime);
            } else if (action == StateActionAdmin.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Cannot reject the event because it has already been published");
                }
                event.setState(EventState.CANCELED);
            }
        }
    }


    private void applyCommonUpdate(Event event,
                                   String annotation,
                                   String description,
                                   String title,
                                   Long categoryId,
                                   Boolean paid,
                                   Integer participantLimit,
                                   Boolean requestModeration,
                                   ru.practicum.main.commons.dto.LocationDto locationDto) {

        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (title != null) event.setTitle(title);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }
        if (paid != null) event.setPaid(paid);

        if (participantLimit != null) {
            if (participantLimit < 0) {
                throw new BadRequestException(
                        "Field: participantLimit. Error: must be greater than or equal to 0. Value: " + participantLimit
                );
            }
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) event.setRequestModeration(requestModeration);
        if (locationDto != null) {
            LocationEmbeddable loc = new LocationEmbeddable();
            loc.setLat(locationDto.getLat());
            loc.setLon(locationDto.getLon());
            event.setLocation(loc);
        }
    }
}
