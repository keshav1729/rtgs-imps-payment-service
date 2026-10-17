package com.payment.rtgsimps.domain;
import jakarta.persistence.*;import java.time.Instant;import java.util.UUID;
@Entity @Table(name="outbox_events") public class OutboxEvent {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(name="aggregate_id",nullable=false) private UUID aggregateId;
 @Column(name="event_type",nullable=false,length=100) private String eventType; @Column(nullable=false,columnDefinition="text") private String payload;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now(); @Column(name="published_at") private Instant publishedAt;
 @Column(nullable=false) private int attempts=0; @Column(name="last_error",length=500) private String lastError;
 protected OutboxEvent(){} public OutboxEvent(UUID aggregateId,String type,String payload){this.aggregateId=aggregateId;this.eventType=type;this.payload=payload;}
 public UUID getId(){return id;} public UUID getAggregateId(){return aggregateId;} public String getPayload(){return payload;} public Instant getPublishedAt(){return publishedAt;}
 public void markPublished(){publishedAt=Instant.now();} public void recordFailure(String error){attempts++;lastError=error==null?"Unknown":error.substring(0,Math.min(500,error.length()));}
}
