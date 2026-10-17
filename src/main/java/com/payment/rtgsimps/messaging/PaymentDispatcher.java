package com.payment.rtgsimps.messaging;

import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.domain.PaymentStatus;
import com.payment.rtgsimps.repository.PaymentRepository;
import com.payment.rtgsimps.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class PaymentDispatcher {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentDispatcher.class);

    private final PaymentRepository payments;
    private final PaymentService paymentService;

    public PaymentDispatcher(
            PaymentRepository payments,
            PaymentService paymentService) {
        this.payments = payments;
        this.paymentService = paymentService;
    }

    @Scheduled(
            fixedDelayString =
                    "${app.payment-dispatcher.poll-delay-ms:1000}"
    )
    public void dispatch() {
        var pending = payments.findTop50ByStatusOrderByCreatedAtAsc(
                PaymentStatus.CREATED
        );

        for (Payment payment : pending) {
            try {
                processIfClaimed(payment);
            } catch (Exception ex) {
                log.error(
                        "Dispatcher failed for payment {}",
                        payment.getId(),
                        ex
                );
            }
        }
    }

    @Transactional
    public void processIfClaimed(Payment payment) {
        int claimed = payments.claimForProcessing(
                payment.getId(),
                PaymentStatus.CREATED,
                PaymentStatus.PROCESSING,
                Instant.now()
        );

        if (claimed == 1) {
            paymentService.process(payment.getId());
        }
    }
}