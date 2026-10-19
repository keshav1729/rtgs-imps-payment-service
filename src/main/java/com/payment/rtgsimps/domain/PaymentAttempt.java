package com.payment.rtgsimps.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_attempts")
public class PaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "provider_name", nullable = false, length = 80)
    private String providerName;

    @Column(name = "provider_reference", length = 128)
    private String providerReference;

    @Column(nullable = false, length = 32)
    private String outcome;

    @Column(name = "provider_code", length = 80)
    private String providerCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected PaymentAttempt() {
    }

    public PaymentAttempt(
            UUID paymentId,
            String providerName,
            String providerReference,
            String outcome,
            String providerCode
    ) {
        this.paymentId = paymentId;
        this.providerName = providerName;
        this.providerReference = providerReference;
        this.outcome = outcome;
        this.providerCode = providerCode;
    }
}
