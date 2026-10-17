package com.payment.rtgsimps.provider;
import com.payment.rtgsimps.domain.*;import org.springframework.context.annotation.Profile;import org.springframework.stereotype.Component;import java.util.UUID;
/** LOCAL DEMO ONLY. Does not move money. */
@Component @Profile({"local","test"}) public class MockBankTransferProvider implements BankTransferProvider {
 public String providerName(){return "mock-provider";}
 public ProviderOutcome submit(Payment p){return new ProviderOutcome(PaymentStatus.SUCCESS,"MOCK-"+UUID.randomUUID(),null,null);}
 public ProviderOutcome inquire(Payment p){return new ProviderOutcome(p.getStatus(),p.getProviderReference(),null,null);}
}
