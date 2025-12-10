package ru.practicum.ewm.main.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.commons.dto.LocationDto;
import ru.practicum.main.commons.enums.EventState;
import ru.practicum.main.commons.model.LocationEmbeddable;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {

    public static Event toEntity(NewEventDto dto,
                                 Category category,
                                 User initiator,
                                 DateTimeFormatter formatter,
                                 LocalDateTime now) {

        LocationEmbeddable loc = new LocationEmbeddable();
        if (dto.getLocation() != null) {
            loc.setLat(dto.getLocation().getLat());
            loc.setLon(dto.getLocation().getLon());
        }

        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .initiator(initiator)
                .eventDate(LocalDateTime.parse(dto.getEventDate(), formatter))
                .createdOn(now)
                .publishedOn(null)
                .location(loc)
                .paid(dto.getPaid() != null ? dto.getPaid() : Boolean.FALSE)
                .participantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0)
                .requestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : Boolean.TRUE)
                .state(EventState.PENDING)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }

    public static EventShortDto toShortDto(Event e,
                                           CategoryDto catDto,
                                           UserShortDto userShort,
                                           DateTimeFormatter formatter,
                                           Long confirmed,
                                           Long views) {
        String eventDate = e.getEventDate() == null ? null : e.getEventDate().format(formatter);
        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(catDto)
                .eventDate(eventDate)
                .initiator(userShort)
                .paid(e.getPaid())
                .title(e.getTitle())
                .confirmedRequests(confirmed == null ? 0L : confirmed)
                .views(views == null ? 0L : views)
                .build();
    }

    public static EventFullDto toFullDto(Event e,
                                         CategoryDto catDto,
                                         UserShortDto userShort,
                                         DateTimeFormatter formatter,
                                         Long confirmed,
                                         Long views) {
        String eventDate = e.getEventDate() == null ? null : e.getEventDate().format(formatter);
        String createdOn = e.getCreatedOn() == null ? null : e.getCreatedOn().format(formatter);
        String publishedOn = e.getPublishedOn() == null ? null : e.getPublishedOn().format(formatter);

        LocationDto loc = null;
        if (e.getLocation() != null) {
            loc = new LocationDto(e.getLocation().getLat(), e.getLocation().getLon());
        }

        return EventFullDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .category(catDto)
                .initiator(userShort)
                .eventDate(eventDate)
                .createdOn(createdOn)
                .publishedOn(publishedOn)
                .location(loc)
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .state(e.getState())
                .confirmedRequests(confirmed == null ? 0L : confirmed)
                .views(views == null ? 0L : views)
                .build();
    }
}
