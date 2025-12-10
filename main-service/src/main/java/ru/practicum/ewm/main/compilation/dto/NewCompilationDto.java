package ru.practicum.ewm.main.compilation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    private List<Long> events;

    @Builder.Default
    private Boolean pinned = Boolean.FALSE;

    @NotBlank(message = "must not be blank")
    @Size(min = 1, max = 50, message = "size must be between 1 and 50")
    private String title;
}
