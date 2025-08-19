package ru.example.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendSync(String topic, String message) throws Exception {
        kafkaTemplate.send(topic, message).get(); // ждём подтверждения
    }
}
