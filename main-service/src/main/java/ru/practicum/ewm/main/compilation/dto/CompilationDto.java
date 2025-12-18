package ru.practicum.ewm.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.main.event.dto.EventShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;

    @Size(min = 1, max = 50, message = "size must be between 1 and 50")
    private String title;

    private Boolean pinned;

    private List<EventShortDto> events;
}
