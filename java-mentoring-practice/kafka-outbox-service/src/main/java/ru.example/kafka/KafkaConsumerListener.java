package ru.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "${kafka.topic}", groupId = "kafka-outbox-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received message from Kafka: " + record.value());
    }
}
