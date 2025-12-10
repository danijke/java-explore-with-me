package ru.practicum.ewm.main.commons.dto.error;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
public class ApiError {

    private String status;

    private String reason;

    private String message;

    private String timestamp;

    private List<String> errors;
}
