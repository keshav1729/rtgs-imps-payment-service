package com.payment.rtgsimps.controller;

import com.payment.rtgsimps.api.CreatePaymentRequest;
import com.payment.rtgsimps.api.PaymentResponse;
import com.payment.rtgsimps.domain.PaymentMode;
import com.payment.rtgsimps.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Constructor injection
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates an IMPS payment.
     */
    @PostMapping("/imps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PaymentResponse createImpsPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication
    ) {
        String customerId = getCustomerId(authentication);

        return paymentService.create(
                PaymentMode.IMPS,
                customerId,
                idempotencyKey,
                request
        );
    }

    /**
     * Creates an RTGS payment.
     */
    @PostMapping("/rtgs")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PaymentResponse createRtgsPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication
    ) {
        String customerId = getCustomerId(authentication);

        return paymentService.create(
                PaymentMode.RTGS,
                customerId,
                idempotencyKey,
                request
        );
    }

    /**
     * Retrieves a payment using its UUID.
     */
    @GetMapping("/{paymentId}")
    public PaymentResponse getPaymentById(
            @PathVariable UUID paymentId,
            Authentication authentication
    ) {
        String customerId = getCustomerId(authentication);

        return paymentService.getById(
                paymentId,
                customerId
        );
    }

    /**
     * Retrieves a payment using its reference.
     */
    @GetMapping("/reference/{reference}")
    public PaymentResponse getPaymentByReference(
            @PathVariable String reference,
            Authentication authentication
    ) {
        String customerId = getCustomerId(authentication);

        return paymentService.getByReference(
                reference,
                customerId
        );
    }

    /**
     * Extracts and validates the authenticated customer's identity.
     */
    private String getCustomerId(Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new IllegalStateException(
                    "Missing customer identity"
            );
        }

        return authentication.getName();
    }
}
