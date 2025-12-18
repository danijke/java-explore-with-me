package ru.practicum.ewm.main.event.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.ewm.main.commons.dto.LocationDto;

@Data
public class NewEventDto {

    @NotBlank(message = "must not be blank")
    @Size(min = 20, max = 2000, message = "size must be between 20 and 2000")
    private String annotation;

    @NotNull(message = "must not be null")
    private Long category;

    @NotBlank(message = "must not be blank")
    @Size(min = 20, max = 7000, message = "size must be between 20 and 7000")
    private String description;

    @NotBlank(message = "must not be blank")
    private String eventDate;

    @NotNull(message = "must not be null")
    private LocationDto location;

    private Boolean paid = Boolean.FALSE;

    @jakarta.validation.constraints.PositiveOrZero(message = "must be greater than or equal to 0")
    private Integer participantLimit = 0;

    private Boolean requestModeration = Boolean.TRUE;

    @NotBlank(message = "must not be blank")
    @Size(min = 3, max = 120, message = "size must be between 3 and 120")
    private String title;
}
