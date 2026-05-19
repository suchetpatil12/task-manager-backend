package com.example.taskmanager.taskmanager_backend.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================
    // COMMON ERROR RESPONSE
    // =========================================

    private Map<String, Object> buildErrorResponse(

            HttpStatus status,

            String error,

            String message,

            HttpServletRequest request

    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                status.value()
        );

        response.put(
                "error",
                error
        );

        response.put(
                "message",
                message
        );

        response.put(
                "path",
                request.getRequestURI()
        );

        return response;
    }

    // =========================================
    // RUNTIME EXCEPTION
    // =========================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(

            RuntimeException ex,

            HttpServletRequest request

    ) {

        return ResponseEntity

                .status(HttpStatus.BAD_REQUEST)

                .body(

                        buildErrorResponse(

                                HttpStatus.BAD_REQUEST,

                                "Bad Request",

                                ex.getMessage(),

                                request

                        )

                );
    }

    // =========================================
    // VALIDATION EXCEPTION
    // =========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(

            MethodArgumentNotValidException ex,

            HttpServletRequest request

    ) {

        String message =

                ex.getBindingResult()

                        .getFieldError()

                        .getDefaultMessage();

        return ResponseEntity

                .status(HttpStatus.BAD_REQUEST)

                .body(

                        buildErrorResponse(

                                HttpStatus.BAD_REQUEST,

                                "Validation Failed",

                                message,

                                request

                        )

                );
    }

    // =========================================
    // ACCESS DENIED
    // =========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(

            AccessDeniedException ex,

            HttpServletRequest request

    ) {

        return ResponseEntity

                .status(HttpStatus.FORBIDDEN)

                .body(

                        buildErrorResponse(

                                HttpStatus.FORBIDDEN,

                                "Access Denied",

                                "You are not authorized to perform this action",

                                request

                        )

                );
    }

    // =========================================
    // GENERIC EXCEPTION
    // =========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(

            Exception ex,

            HttpServletRequest request

    ) {

        return ResponseEntity

                .status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(

                        buildErrorResponse(

                                HttpStatus.INTERNAL_SERVER_ERROR,

                                "Internal Server Error",

                                ex.getMessage(),

                                request

                        )

                );
    }
}