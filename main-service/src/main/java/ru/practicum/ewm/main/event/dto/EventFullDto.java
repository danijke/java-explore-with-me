package ru.practicum.ewm.main.event.dto;

import lombok.*;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.commons.dto.LocationDto;
import ru.practicum.ewm.main.commons.enums.EventState;
import ru.practicum.ewm.main.user.dto.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private UserShortDto initiator;
    private String eventDate;
    private String createdOn;
    private String publishedOn;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private Long confirmedRequests;
    private Long views;
}
