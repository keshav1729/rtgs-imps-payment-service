package com.payment.rtgsimps.provider;
import com.payment.rtgsimps.domain.Payment;
public interface BankTransferProvider {
    String providerName();ProviderOutcome submit(Payment payment);ProviderOutcome inquire(Payment payment);
}
