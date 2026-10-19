package com.payment.rtgsimps.exception;

import com.payment.rtgsimps.api.ApiError;
import com.payment.rtgsimps.service.IdempotencyConflictException;
import com.payment.rtgsimps.service.PaymentNotFoundException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiError> handlePaymentNotFound() {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Payment not found",
                Map.of()
        );
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ApiError> handleIdempotencyConflict() {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "Idempotency key conflicts with existing request",
                Map.of()
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                Map.of()
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(
            MissingRequestHeaderException exception
    ) {
        String message = "Missing header: " + exception.getHeaderName();

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                Map.of()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException exception
    ) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "Request cannot be processed in current state",
                Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalServerError(
            Exception exception
    ) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                Map.of()
        );
    }

    private ResponseEntity<ApiError> createErrorResponse(
            HttpStatus status,
            String message,
            Map<String, String> details
    ) {
        ApiError apiError = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity
                .status(status)
                .body(apiError);
    }
}
