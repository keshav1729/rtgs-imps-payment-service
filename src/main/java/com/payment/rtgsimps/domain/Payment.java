package com.payment.rtgsimps.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="payments",indexes={@Index(name="idx_payments_customer_created",columnList="customer_id,created_at"),@Index(name="idx_payments_status_created",columnList="status,created_at")})
public class Payment {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(name="customer_id",nullable=false,length=128) private String customerId;
 @Column(name="beneficiary_id",nullable=false,length=128) private String beneficiaryId;
 @Column(name="idempotency_key",nullable=false,length=128) private String idempotencyKey;
 @Column(name="request_hash",nullable=false,length=64) private String requestHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private PaymentMode mode;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
 @Column(nullable=false,length=3) private String currency;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private PaymentStatus status;
 @Column(name="client_reference",nullable=false,length=100) private String clientReference;
 @Column(name="provider_reference",length=128) private String providerReference;
 @Column(name="failure_code",length=80) private String failureCode;
 @Column(name="failure_message",length=300) private String failureMessage;
 @Version @Column(nullable=false) private long version;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Payment(){}
 public Payment(String customerId,String beneficiaryId,String key,String hash,PaymentMode mode,BigDecimal amount,String currency,String reference){
 this.customerId=customerId;this.beneficiaryId=beneficiaryId;this.idempotencyKey=key;this.requestHash=hash;this.mode=mode;this.amount=amount;this.currency=currency;this.clientReference=reference;this.status=PaymentStatus.CREATED;this.createdAt=Instant.now();this.updatedAt=createdAt;}
 public void transitionTo(PaymentStatus next){boolean ok=switch(status){case CREATED->next==PaymentStatus.PROCESSING||next==PaymentStatus.CANCELLED;case PROCESSING->next==PaymentStatus.PENDING||next==PaymentStatus.SUCCESS||next==PaymentStatus.FAILED||next==PaymentStatus.UNKNOWN;case PENDING,UNKNOWN->next==PaymentStatus.PROCESSING||next==PaymentStatus.SUCCESS||next==PaymentStatus.FAILED||next==PaymentStatus.CANCELLED;case SUCCESS,FAILED,CANCELLED->false;};if(!ok)throw new IllegalStateException("Invalid payment transition");status=next;updatedAt=Instant.now();}
 public void setProviderResult(String ref,PaymentStatus next,String code,String message){if(ref!=null&&!ref.isBlank())providerReference=ref;failureCode=code;failureMessage=message;transitionTo(next);}
 public UUID getId(){return id;} public String getCustomerId(){return customerId;} public String getBeneficiaryId(){return beneficiaryId;} public String getIdempotencyKey(){return idempotencyKey;} public String getRequestHash(){return requestHash;} public PaymentMode getMode(){return mode;} public BigDecimal getAmount(){return amount;} public String getCurrency(){return currency;} public PaymentStatus getStatus(){return status;} public String getClientReference(){return clientReference;} public String getProviderReference(){return providerReference;} public String getFailureCode(){return failureCode;} public long getVersion(){return version;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
