package ru.practicum.ewm.main.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.main.commons.dto.error.ApiError;
import ru.practicum.ewm.main.exception.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestControllerAdvice
public class ExceptionAdvice {

    private final DateTimeFormatter formatter;

    public ExceptionAdvice(DateTimeFormatter apiDateTimeFormatter) {
        this.formatter = apiDateTimeFormatter;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError body = buildError(
                "The required object was not found.",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        ApiError body = buildError(
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        ex.getMostSpecificCause();
        String mostSpecific = ex.getMostSpecificCause().getMessage();
        ApiError body = buildError(
                "Integrity constraint has been violated.",
                mostSpecific,
                HttpStatus.CONFLICT,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError body = buildError(
                "Incorrectly made request.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        ApiError body = buildError(
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String value = fieldError.getRejectedValue() == null ? "null" : fieldError.getRejectedValue().toString();
            String msg = "Field: " + fieldError.getField() + ". Error: " + fieldError.getDefaultMessage() + ". Value: " + value;
            errors.add(msg);
        });
        String first = errors.isEmpty() ? "Validation failed" : errors.get(0);
        ApiError body = buildError(
                "Incorrectly made request.",
                first,
                HttpStatus.BAD_REQUEST,
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
            String msg = "Field: " + path + ". Error: " + violation.getMessage() + ". Value: " + String.valueOf(violation.getInvalidValue());
            errors.add(msg);
        });
        String first = errors.isEmpty() ? "Validation failed" : errors.get(0);
        ApiError body = buildError(
                "Incorrectly made request.",
                first,
                HttpStatus.BAD_REQUEST,
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        ApiError body = buildError("Incorrectly made request.", ex.getMessage(), HttpStatus.BAD_REQUEST, Collections.emptyList());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        ex.getMostSpecificCause();
        String msg = ex.getMostSpecificCause().getMessage();
        ApiError body = buildError("Incorrectly made request.", msg, HttpStatus.BAD_REQUEST, Collections.emptyList());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        ApiError body = buildError(
                "Unexpected error.",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                Collections.emptyList()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError buildError(String reason, String message, HttpStatus status, List<String> errors) {
        ApiError apiError = new ApiError();
        apiError.setReason(reason);
        apiError.setMessage(message);
        apiError.setStatus(status.name());
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        apiError.setErrors(errors);
        return apiError;
    }
}
