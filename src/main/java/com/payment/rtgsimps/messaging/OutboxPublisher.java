package com.payment.rtgsimps.messaging;
import com.payment.rtgsimps.domain.OutboxEvent;import com.payment.rtgsimps.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;import org.springframework.kafka.core.KafkaTemplate;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;
@Component public class OutboxPublisher {
 private final OutboxEventRepository repo;private final KafkaTemplate<String,String> kafka;private final boolean enabled;private final String topic;
 public OutboxPublisher(OutboxEventRepository r,KafkaTemplate<String,String> k,@Value("${app.outbox.enabled:false}") boolean e,@Value("${app.kafka.payment-topic:payment-events}") String t){repo=r;kafka=k;enabled=e;topic=t;}
 @Scheduled(fixedDelayString="${app.outbox.poll-delay-ms:1000}") public void publishBatch(){if(!enabled)return;for(OutboxEvent e:repo.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()){try{kafka.send(topic,e.getAggregateId().toString(),e.getPayload()).get();repo.findById(e.getId()).ifPresent(x->{x.markPublished();repo.save(x);});}catch(Exception ex){repo.findById(e.getId()).ifPresent(x->{x.recordFailure(ex.getMessage());repo.save(x);});break;}}}
}
