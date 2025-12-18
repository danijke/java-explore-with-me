package ru.practicum.ewm.main.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
