package ru.practicum.ewm.main.category.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class CategoryDto {
    @Null
    Long id;

    @NotBlank
    @Size(max = 50)
    String name;
}

