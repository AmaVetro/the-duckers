package com.theduckers.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;


//@RestControllerAdvice(basePackages = "com.theduckers.backend.controller")
public class GlobalExceptionHandler {


        
        // =========================
        // INVALID INPUT (DTO validation)
        // =========================
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                MethodArgumentNotValidException ex
        ) {
                String message = ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .orElse("Invalid request");

                ErrorResponse response = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        message
                );

                return ResponseEntity.badRequest().body(response);
        }
        


        // =========================
        // BUSINESS ERRORS
        // =========================
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                IllegalArgumentException ex
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage()
                );

                return ResponseEntity.badRequest().body(response);
        }


        
        // =========================
        // FALLBACK (unexpected errors)
        // =========================
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(
                Exception ex
        ) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Unexpected error occurred"
                );

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
}

