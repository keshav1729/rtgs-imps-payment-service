package com.payment.rtgsimps.config;

import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.provider.BankTransferProvider;
import com.payment.rtgsimps.provider.ProviderOutcome;
import com.payment.rtgsimps.provider.ProviderUnavailableException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProviderSelectionConfig {

    
    @Bean
    @Profile("!local & !test")
    public BankTransferProvider unconfiguredProvider() {

        return new BankTransferProvider() {

            @Override
            public String providerName() {
                return "unconfigured-provider";
            }

            @Override
            public ProviderOutcome submit(Payment payment) {
                throw new ProviderUnavailableException(
                        "No real bank provider configured"
                );
            }

            @Override
            public ProviderOutcome inquire(Payment payment) {
                throw new ProviderUnavailableException(
                        "No real bank provider configured"
                );
            }
        };
    }
}
