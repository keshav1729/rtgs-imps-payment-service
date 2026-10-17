package com.payment.rtgsimps.domain;
import org.junit.jupiter.api.Test;import java.math.BigDecimal;import static org.junit.jupiter.api.Assertions.*;
class PaymentStateMachineTest {
 private Payment payment(){return new Payment("c","b","k","hash",PaymentMode.IMPS,new BigDecimal("10.00"),"INR","ref");}
 @Test void permitsProcessingToSuccess(){Payment p=payment();p.transitionTo(PaymentStatus.PROCESSING);p.transitionTo(PaymentStatus.SUCCESS);assertEquals(PaymentStatus.SUCCESS,p.getStatus());}
 @Test void rejectsTerminalTransition(){Payment p=payment();p.transitionTo(PaymentStatus.PROCESSING);p.transitionTo(PaymentStatus.SUCCESS);assertThrows(IllegalStateException.class,()->p.transitionTo(PaymentStatus.FAILED));}
}
