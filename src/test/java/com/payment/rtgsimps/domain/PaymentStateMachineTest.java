package com.payment.rtgsimps.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStateMachineTest {

    private Payment createPayment() {
        return new Payment(
                "c",
                "b",
                "k",
                "hash",
                PaymentMode.IMPS,
                new BigDecimal("10.00"),
                "INR",
                "ref"
        );
    }

    @Test
    void permitsProcessingToSuccess() {
        Payment payment = createPayment();

        payment.transitionTo(PaymentStatus.PROCESSING);
        payment.transitionTo(PaymentStatus.SUCCESS);

        assertEquals(
                PaymentStatus.SUCCESS,
                payment.getStatus()
        );
    }

    @Test
    void rejectsTransitionFromTerminalState() {
        Payment payment = createPayment();

        payment.transitionTo(PaymentStatus.PROCESSING);
        payment.transitionTo(PaymentStatus.SUCCESS);

        assertThrows(
                IllegalStateException.class,
                () -> payment.transitionTo(PaymentStatus.FAILED)
        );
    }
}
