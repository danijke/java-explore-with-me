package ru.practicum.ewm.main.event.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.main.commons.dto.LocationDto;
import ru.practicum.main.commons.enums.StateActionUser;

@Data
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "size must be between 20 and 2000")
    private String annotation;

    @Size(min = 20, max = 7000, message = "size must be between 20 and 7000")
    private String description;

    @Size(min = 3, max = 120, message = "size must be between 3 and 120")
    private String title;

    private Long category;
    private Boolean paid;

    @PositiveOrZero(message = "must be greater than or equal to 0")
    private Integer participantLimit;

    private Boolean requestModeration;

    private String eventDate;

    private LocationDto location;

    private StateActionUser stateAction;
}
