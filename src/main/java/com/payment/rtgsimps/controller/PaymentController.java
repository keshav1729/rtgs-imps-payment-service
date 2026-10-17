package com.payment.rtgsimps.controller;
import com.payment.rtgsimps.api.*;import com.payment.rtgsimps.domain.PaymentMode;import com.payment.rtgsimps.service.PaymentService;
import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.UUID;
@RestController @RequestMapping("/api/v1/payments") public class PaymentController {
 private final PaymentService service;public PaymentController(PaymentService s){service=s;}
 @PostMapping("/imps") @ResponseStatus(HttpStatus.ACCEPTED) public PaymentResponse imps(@RequestHeader("Idempotency-Key") String key,@RequestHeader(value="X-Request-Id",required=false) String requestId,@Valid @RequestBody CreatePaymentRequest r,Authentication a){return service.create(PaymentMode.IMPS,customer(a),key,r);}
 @PostMapping("/rtgs") @ResponseStatus(HttpStatus.ACCEPTED) public PaymentResponse rtgs(@RequestHeader("Idempotency-Key") String key,@RequestHeader(value="X-Request-Id",required=false) String requestId,@Valid @RequestBody CreatePaymentRequest r,Authentication a){return service.create(PaymentMode.RTGS,customer(a),key,r);}
 @GetMapping("/{paymentId}") public PaymentResponse get(@PathVariable UUID paymentId,Authentication a){return service.getById(paymentId,customer(a));}
 @GetMapping("/reference/{reference}") public PaymentResponse reference(@PathVariable String reference,Authentication a){return service.getByReference(reference,customer(a));}
 private String customer(Authentication a){if(a==null||a.getName()==null||a.getName().isBlank())throw new IllegalStateException("Missing customer identity");return a.getName();}
}
