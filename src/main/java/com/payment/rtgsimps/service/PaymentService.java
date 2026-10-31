
package com.payment.rtgsimps.service;

import com.fasterxml.jackson.core.JsonProcessingException;


import com.payment.rtgsimps.api.*;
import com.payment.rtgsimps.domain.*;
import com.payment.rtgsimps.provider.*;
import com.payment.rtgsimps.repository.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

 private final PaymentRepository payments;
 private final OutboxEventRepository outbox;
 private final BankTransferProvider provider;
 private final PaymentResultService resultService;

 public PaymentService(
         PaymentRepository payments,
         OutboxEventRepository outbox,
         BankTransferProvider provider,
         PaymentResultService resultService) {

  this.payments = payments;
  this.outbox = outbox;
  this.provider = provider;
  this.resultService = resultService;
 }

 @Transactional
 public PaymentResponse create(
         PaymentMode mode,
         String customer,
         String key,
         CreatePaymentRequest request) {

  if (key == null || key.isBlank() || key.length() > 128) {
   throw new IllegalArgumentException(
           "Invalid Idempotency-Key"
   );
  }

  String requestHash = hash(
          mode + "|"
                  + request.beneficiaryId() + "|"
                  + request.amount().toPlainString() + "|"
                  + request.currency() + "|"
                  + request.reference()
  );

  var existing =
          payments.findByCustomerIdAndIdempotencyKey(
                  customer,
                  key
          );

  if (existing.isPresent()) {
   Payment old = existing.get();

   if (!old.getRequestHash().equals(requestHash)) {
    throw new IdempotencyConflictException();
   }

   return PaymentResponse.from(old);
  }

  Payment payment = new Payment(
          customer,
          request.beneficiaryId(),
          key,
          requestHash,
          mode,
          request.amount(),
          request.currency(),
          request.reference()
  );

  try {
   payments.saveAndFlush(payment);
  } catch (DataIntegrityViolationException ex) {
   throw new IdempotencyConflictException();
  }

  // Payment abhi CREATED rahegi.
  // Dispatcher baad mein ise PROCESSING mein claim karega.
  outbox.save(
          new OutboxEvent(
                  payment.getId(),
                  "PAYMENT_CREATED",
                  json(Map.of(
                          "paymentId",
                          payment.getId().toString(),
                          "mode",
                          mode.name(),
                          "status",
                          payment.getStatus().name()
                  ))
          )
  );

  return PaymentResponse.from(payment);
 }

 @Transactional(readOnly = true)
 public PaymentResponse getById(
         UUID id,
         String customer) {

  Payment payment =
          payments.findByIdAndCustomerId(id, customer)
                  .orElseThrow(PaymentNotFoundException::new);

  return PaymentResponse.from(payment);
 }

 @Transactional(readOnly = true)
 public PaymentResponse getByReference(
         String reference,
         String customer) {

  Payment payment =
          payments.findByCustomerIdAndClientReference(
                  customer,
                  reference
          ).orElseThrow(PaymentNotFoundException::new);

  return PaymentResponse.from(payment);
 }

 public PaymentResponse process(UUID id) {

  Payment payment = payments.findById(id)
          .orElseThrow(PaymentNotFoundException::new);

  if (payment.getStatus() != PaymentStatus.PROCESSING) {
   return PaymentResponse.from(payment);
  }

  ProviderOutcome outcome;

  try {
   outcome = provider.submit(payment);
  } catch (RuntimeException ex) {
   // Provider call ka result uncertain ho sakta hai.
   // Blindly payment dobara submit nahi karna.
   return resultService.recordUnknown(
           id,
           provider.providerName()
   );
  }

  return resultService.recordResult(
          id,
          provider.providerName(),
          outcome
  );
 }

 private String hash(String value) {
  try {
   byte[] digest = MessageDigest
           .getInstance("SHA-256")
           .digest(value.getBytes(StandardCharsets.UTF_8));

   return HexFormat.of().formatHex(digest);

  } catch (NoSuchAlgorithmException ex) {
   throw new IllegalStateException(ex);
  }
 }
}
