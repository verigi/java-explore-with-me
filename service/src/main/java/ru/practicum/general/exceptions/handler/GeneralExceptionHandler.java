package ru.practicum.general.exceptions.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.general.dto.error.ApiErrorDto;
import ru.practicum.general.exceptions.CustomAccessException;
import ru.practicum.general.exceptions.CustomConflictException;
import ru.practicum.general.exceptions.DuplicationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GeneralExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationException(MethodArgumentNotValidException e) {
        return errorBuilder("Validation error",
                "Invalid arguments",
                HttpStatus.BAD_REQUEST,
                extractErrors(e));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return errorBuilder("Value fetch error",
                "Invalid value",
                HttpStatus.BAD_REQUEST,
                extractErrors(e));
    }

    @ExceptionHandler(CustomAccessException.class)
    public ResponseEntity<ApiErrorDto> handleCustomAccessException(CustomAccessException e) {
        return errorBuilder("Access denied",
                "Not enough user permissions",
                HttpStatus.BAD_REQUEST,
                extractErrors(e));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleEntityNotFoundException(EntityNotFoundException e) {
        return errorBuilder("Object was not found",
                "Entity was not found in DB",
                HttpStatus.NOT_FOUND,
                extractErrors(e));
    }

    @ExceptionHandler(CustomConflictException.class)
    public ResponseEntity<ApiErrorDto> handleCustomConflictException(CustomConflictException e) {
        return errorBuilder("Check values of the request",
                "For the requested operation the conditions are not met",
                HttpStatus.CONFLICT,
                extractErrors(e));
    }

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ApiErrorDto> handleDuplicationException(DuplicationException e) {
        return errorBuilder("Invalid user data",
                "Duplicate data",
                HttpStatus.CONFLICT,
                extractErrors(e));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiErrorDto> handleGeneralException(Exception e) {
//        return errorBuilder("Unexpected error",
//                "Something went wrong",
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                extractErrors(e));
//    }

    private ResponseEntity<ApiErrorDto> errorBuilder(String message, String reason, HttpStatus status, List<String> errors) {
        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .errors(errors).build();

        return new ResponseEntity<>(apiErrorDto, status);
    }

    private List<String> extractErrors(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(e.getLocalizedMessage());
    }
}