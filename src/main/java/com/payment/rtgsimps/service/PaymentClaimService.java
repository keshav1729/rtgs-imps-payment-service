
package com.payment.rtgsimps.service;

import com.payment.rtgsimps.domain.PaymentStatus;
import com.payment.rtgsimps.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentClaimService {

    private final PaymentRepository payments;

    public PaymentClaimService(PaymentRepository payments) {
        this.payments = payments;
    }

    @Transactional
    public boolean claim(UUID paymentId) {
        int updated = payments.claimForProcessing(
                paymentId,
                PaymentStatus.CREATED,
                PaymentStatus.PROCESSING,
                Instant.now()
        );

        return updated == 1;
    }
}
