package com.payment.rtgsimps.provider;
import com.payment.rtgsimps.domain.PaymentStatus;
public record ProviderOutcome(PaymentStatus status,String providerReference,String failureCode,String failureMessage){}
