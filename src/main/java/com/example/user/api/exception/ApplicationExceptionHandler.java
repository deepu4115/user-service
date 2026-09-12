package com.example.user.api.exception;

import com.example.user.domain.exception.InvalidCredentialsException;
import com.example.user.domain.exception.UserNotFoundByIdException;
import com.example.user.shared.responsewrappers.ErrorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    public <T> ResponseEntity<ErrorModel<T>> errorResponse(HttpStatus status, String message, T rootCause) {
        return ResponseEntity.status(status).body(ErrorModel.<T>builder()
                .status(status.value())
                .message(message)
                .rootCause(rootCause)
                .build());
    }

    @ExceptionHandler(UserNotFoundByIdException.class)
    public ResponseEntity<ErrorModel<String>> handleUserNotFoundByIdException(UserNotFoundByIdException ex) {
        log.error("User not found exception: {}", ex.getMessage());
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "User not found by the given ID");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorModel<String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error("Invalid credentials exception: {}", ex.getMessage());
        return errorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Authentication failed due to invalid credentials");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorModel<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorModel<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid input provided");
    }
}
