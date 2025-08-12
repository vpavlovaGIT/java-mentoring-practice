package ru.example.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.example.entity.OutboxEvent;
import ru.example.kafka.KafkaProducerService;
import ru.example.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    // Проблема №1 Обработка большого объема записей в таблице Outbox
    // Это приведет к перегрузке памяти (OutOfMemoryError)
    //
    // Решение: обрабатывать события порциями (Pageable) и коммитить изменения частями
    //
    // Проблема №2 Наложение job-ов, т. е. новая задача запустится до завершения предыдущей:
    // - конкурирующие потоки будут обрабатывать одни и те же сообщения, что приведет к дублированию отправки в Kafka;
    // - гонка данных при обновлении поля sent;
    //
    // Решение: сделать блокировку записей и подтверждение отправки
    //
    // Факультативный вопрос про N реплик приложения ?
    // Нет, корректно работать не будет, так как все реплики будут обрабатывать одни и те же сообщения.
    private final OutboxRepository repository;
    private final KafkaProducerService kafkaProducer;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${outbox.batch-size:1000}")
    private int batchSize;

    @Scheduled(fixedDelay = 10000) // каждые 10 секунд, только после завершения предыдущей
    @Transactional
    public void publishOutboxEvents() {
        while (true) {
            // Забираем партию неотправленных сообщений
            List<OutboxEvent> events = repository.findTopUnsent(PageRequest.of(0, batchSize));
            if (events.isEmpty()) {
                break;
            }
            // Отправляем в Kafka
            for (OutboxEvent event : events) {
                kafkaProducer.send(topic, event.getPayload());
                event.setSent(true);
            }
            repository.saveAll(events); // одним батчем
        }
    }
}
