package org.example.java_web_service_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        return buildError(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", errors, "/api/v1");
    }

    // Lỗi nghiệp vụ tùy chỉnh
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
        return buildError(ex.getStatus(), ex.getMessage(), null, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi hệ thống: " + ex.getMessage(), null, null);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message, Object details, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null) body.put("details", details);
        if (path != null) body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}

