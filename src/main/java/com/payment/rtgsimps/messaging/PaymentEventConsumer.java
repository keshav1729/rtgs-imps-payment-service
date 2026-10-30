package com.payment.rtgsimps.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @KafkaListener(
            topics = "${app.kafka.payment-topic:payment-events}",
            groupId = "${spring.kafka.consumer.group-id:rtgs-imps-service}"
    )
    public void consume(String payload) {
        log.info("Received payment event: {}", payload);
    }
}
