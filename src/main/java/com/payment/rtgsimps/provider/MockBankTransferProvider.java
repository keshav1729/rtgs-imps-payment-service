package com.payment.rtgsimps.provider;

import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.domain.PaymentStatus;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile({"local", "test"})
public class MockBankTransferProvider implements BankTransferProvider {

    @Override
    public String providerName() {
        return "mock-provider";
    }

    @Override
    public ProviderOutcome submit(Payment payment) {

        String mockProviderReference =
                "MOCK-" + UUID.randomUUID();

        return new ProviderOutcome(
                PaymentStatus.SUCCESS,
                mockProviderReference,
                null,
                null
        );
    }

    @Override
    public ProviderOutcome inquire(Payment payment) {

        return new ProviderOutcome(
                payment.getStatus(),
                payment.getProviderReference(),
                null,
                null
        );
    }
}
