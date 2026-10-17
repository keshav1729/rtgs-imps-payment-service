
package com.payment.rtgsimps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.payment.rtgsimps.api.PaymentResponse;
import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.domain.PaymentAttempt;
import com.payment.rtgsimps.domain.PaymentStatus;
import com.payment.rtgsimps.provider.ProviderOutcome;
import com.payment.rtgsimps.repository.OutboxEventRepository;
import com.payment.rtgsimps.repository.PaymentAttemptRepository;
import com.payment.rtgsimps.repository.PaymentRepository;
import com.payment.rtgsimps.domain.OutboxEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentResultService {

    private static final Set<PaymentStatus> ALLOWED_PROVIDER_STATUSES =
            Set.of(
                    PaymentStatus.PENDING,
                    PaymentStatus.SUCCESS,
                    PaymentStatus.FAILED,
                    PaymentStatus.UNKNOWN
            );

    private final PaymentRepository payments;
    private final PaymentAttemptRepository attempts;
    private final OutboxEventRepository outbox;
    private final ObjectMapper mapper;

    public PaymentResultService(
            PaymentRepository payments,
            PaymentAttemptRepository attempts,
            OutboxEventRepository outbox,
            ObjectMapper mapper) {

        this.payments = payments;
        this.attempts = attempts;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public PaymentResponse recordResult(
            UUID id,
            String providerName,
            ProviderOutcome result) {

        Payment payment = payments.findById(id)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            return PaymentResponse.from(payment);
        }

        if (!ALLOWED_PROVIDER_STATUSES.contains(result.status())) {
            throw new IllegalArgumentException(
                    "Invalid provider payment status"
            );
        }

        payment.setProviderResult(
                result.providerReference(),
                result.status(),
                result.failureCode(),
                result.failureMessage()
        );

        payments.save(payment);

        attempts.save(
                new PaymentAttempt(
                        payment.getId(),
                        providerName,
                        result.providerReference(),
                        result.status().name(),
                        result.failureCode()
                )
        );

        saveEvent(payment);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse recordUnknown(
            UUID id,
            String providerName) {

        Payment payment = payments.findById(id)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            return PaymentResponse.from(payment);
        }

        payment.transitionTo(PaymentStatus.UNKNOWN);
        payments.save(payment);

        attempts.save(
                new PaymentAttempt(
                        payment.getId(),
                        providerName,
                        null,
                        PaymentStatus.UNKNOWN.name(),
                        "PROVIDER_OUTCOME_UNKNOWN"
                )
        );

        saveEvent(payment);

        return PaymentResponse.from(payment);
    }

    private void saveEvent(Payment payment) {
        outbox.save(
                new OutboxEvent(
                        payment.getId(),
                        "PAYMENT_" + payment.getStatus().name(),
                        json(Map.of(
                                "paymentId",
                                payment.getId().toString(),
                                "status",
                                payment.getStatus().name()
                        ))
                )
        );
    }

    private String json(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Event serialization failed",
                    ex
            );
        }
    }
}
