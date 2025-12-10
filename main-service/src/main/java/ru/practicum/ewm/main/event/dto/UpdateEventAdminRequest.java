package ru.practicum.ewm.main.event.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.main.commons.dto.LocationDto;
import ru.practicum.main.commons.enums.StateActionAdmin;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "size must be between 20 and 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "size must be between 20 and 7000")
    private String description;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @jakarta.validation.constraints.PositiveOrZero(message = "must be greater than or equal to 0")
    private Integer participantLimit;

    private Boolean requestModeration;

    private StateActionAdmin stateAction;

    @Size(min = 3, max = 120, message = "size must be between 3 and 120")
    private String title;
}
