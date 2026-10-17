package com.payment.rtgsimps.api;
import com.payment.rtgsimps.domain.*;import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;
public record PaymentResponse(UUID paymentId,String reference,PaymentMode mode,BigDecimal amount,String currency,PaymentStatus status,String providerReference,String failureCode,Instant createdAt,Instant updatedAt){
 public static PaymentResponse from(Payment p){return new PaymentResponse(p.getId(),p.getClientReference(),p.getMode(),p.getAmount(),p.getCurrency(),p.getStatus(),p.getProviderReference(),p.getFailureCode(),p.getCreatedAt(),p.getUpdatedAt());}
}
