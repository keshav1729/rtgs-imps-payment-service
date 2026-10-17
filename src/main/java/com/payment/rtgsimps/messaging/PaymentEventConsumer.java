package com.payment.rtgsimps.messaging;
import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
/** No financial side effect here; implement an inbox/deduplication table before adding business actions. */
@Component public class PaymentEventConsumer {
 @KafkaListener(topics="${app.kafka.payment-topic:payment-events}",groupId="${spring.kafka.consumer.group-id:rtgs-imps-service}")
 public void consume(String payload){ /* schema validation, inbox dedupe, metrics and business handler required */ }
}
