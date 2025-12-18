package ru.practicum.ewm.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "size must be between 1 and 50")
    private String title;
}
