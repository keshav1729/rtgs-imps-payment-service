package com.payment.rtgsimps.api;

import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.domain.PaymentMode;
import com.payment.rtgsimps.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        String reference,
        PaymentMode mode,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String providerReference,
        Instant createdAt,
        Instant updatedAt
) {

    
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getClientReference(),
                payment.getMode(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getProviderReference(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
