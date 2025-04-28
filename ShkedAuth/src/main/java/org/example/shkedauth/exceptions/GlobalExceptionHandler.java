package org.example.shkedauth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UserNotFoundException.class,
            InvalidCredentialsException.class,
            InvalidRefreshTokenException.class,
            ExpiredRefreshTokenException.class,
            InvalidGroupException.class
    })
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException ex) {
        HttpStatus status = resolveHttpStatus(ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Something went wrong. Please try again later.");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus resolveHttpStatus(RuntimeException ex) {
        if (ex instanceof EmailAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof InvalidCredentialsException ||
                ex instanceof InvalidRefreshTokenException ||
                ex instanceof ExpiredRefreshTokenException) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.BAD_REQUEST; // На всякий случай fallback
        }
    }
}
