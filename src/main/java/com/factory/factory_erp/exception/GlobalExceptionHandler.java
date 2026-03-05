package com.factory.factory_erp.exception;

import com.factory.factory_erp.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("INVALID_CREDENTIALS")
                        .details(ex.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder detailsBuilder = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            if (detailsBuilder.length() > 0) {
                detailsBuilder.append("; ");
            }
            detailsBuilder.append(error.getField()).append(": ").append(error.getDefaultMessage());
        });
        
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("VALIDATION_ERROR")
                        .details(detailsBuilder.toString())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message("An error occurred")
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .details(ex.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
