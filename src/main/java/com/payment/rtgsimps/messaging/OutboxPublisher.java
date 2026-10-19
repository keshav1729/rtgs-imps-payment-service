package com.payment.rtgsimps.messaging;

import com.payment.rtgsimps.domain.OutboxEvent;
import com.payment.rtgsimps.repository.OutboxEventRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final boolean enabled;
    private final String topic;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.outbox.enabled:false}") boolean enabled,
            @Value("${app.kafka.payment-topic:payment-events}") String topic
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.enabled = enabled;
        this.topic = topic;
    }

    @Scheduled(
            fixedDelayString = "${app.outbox.poll-delay-ms:1000}"
    )
    public void publishBatch() {

        if (!enabled) {
            return;
        }

        var unpublishedEvents =
                repository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();

        for (OutboxEvent event : unpublishedEvents) {
            try {
                publishEvent(event);

                markEventAsPublished(event);

            } catch (Exception exception) {
                recordPublishingFailure(event, exception);

                break;
            }
        }
    }

    private void publishEvent(OutboxEvent event) throws Exception {

        String kafkaKey = event.getAggregateId().toString();
        String payload = event.getPayload();

        kafkaTemplate
                .send(topic, kafkaKey, payload)
                .get();
    }

    private void markEventAsPublished(OutboxEvent event) {

        repository.findById(event.getId())
                .ifPresent(publishedEvent -> {
                    publishedEvent.markPublished();
                    repository.save(publishedEvent);
                });
    }

    private void recordPublishingFailure(
            OutboxEvent event,
            Exception exception
    ) {
        repository.findById(event.getId())
                .ifPresent(failedEvent -> {
                    failedEvent.recordFailure(exception.getMessage());
                    repository.save(failedEvent);
                });
    }
}
