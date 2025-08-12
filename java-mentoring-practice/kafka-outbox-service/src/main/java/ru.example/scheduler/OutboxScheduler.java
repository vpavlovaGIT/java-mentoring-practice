package ru.example.scheduler;

import lombok.RequiredArgsConstructor;
import ru.example.entity.OutboxEvent;
import ru.example.kafka.KafkaProducerService;
import ru.example.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository repository;
    private final KafkaProducerService kafkaProducer;

    @Value("${kafka.topic}")
    private String topic;

    @Scheduled(fixedRate = 60000) // каждую минуту
    public void publishOutboxEvents() {
        var unsentEvents = repository.findBySentFalse();
        for (OutboxEvent event : unsentEvents) {
            kafkaProducer.send(topic, event.getPayload());
            event.setSent(true);
            repository.save(event);
        }
    }
}
