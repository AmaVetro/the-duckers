package com.theduckers.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// =========================
// exception/GlobalExceptionHandler
// =========================

@RestControllerAdvice(basePackages = "com.theduckers.backend.controller")
public class GlobalExceptionHandler {

        // =========================
        // DTO Validation Errors
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

                return ResponseEntity.badRequest().body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message
                        )
                );
        }

        // =========================
        // Explicit Business Errors
        // =========================
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(
                BadRequestException ex
        ) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage()
                        )
                );
        }

        @ExceptionHandler(InvalidStateException.class)
        public ResponseEntity<ErrorResponse> handleInvalidState(
                InvalidStateException ex
        ) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage()
                        )
                );
        }

        // =========================
        // Illegal Arguments
        // =========================
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                IllegalArgumentException ex
        ) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage()
                        )
                );
        }

        // =========================
        // Fallback
        // =========================
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(
                Exception ex
        ) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Unexpected error occurred"
                        )
                );
        }
}