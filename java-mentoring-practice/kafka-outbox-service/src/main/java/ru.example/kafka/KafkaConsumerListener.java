package ru.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "${kafka.topic}", groupId = "kafka-outbox-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            System.out.println("Received message from Kafka: " + record.value());
            ack.acknowledge(); // вручную фиксируем оффсет
        } catch (Exception e) {
            System.err.println("Error while processing event: " + e.getMessage());
        }
    }
}
