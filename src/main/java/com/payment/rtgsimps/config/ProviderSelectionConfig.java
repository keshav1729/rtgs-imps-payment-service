package com.payment.rtgsimps.config;
import com.payment.rtgsimps.provider.*;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.Profile;
@Configuration public class ProviderSelectionConfig {
 @Bean @Profile("!local & !test") BankTransferProvider unconfiguredProvider(){return new BankTransferProvider(){
  public String providerName(){return "unconfigured-provider";}
  public ProviderOutcome submit(com.payment.rtgsimps.domain.Payment p){throw new ProviderUnavailableException("No real bank provider configured");}
  public ProviderOutcome inquire(com.payment.rtgsimps.domain.Payment p){throw new ProviderUnavailableException("No real bank provider configured");}
 };}
}
