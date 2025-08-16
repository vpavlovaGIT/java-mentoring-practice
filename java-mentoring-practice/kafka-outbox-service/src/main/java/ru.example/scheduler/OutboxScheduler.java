package ru.example.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.example.entity.OutboxEvent;
import ru.example.kafka.KafkaProducerService;
import ru.example.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
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

    @Value("${outbox.thread-pool:10}") // число потоков
    private int threadPool;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> events = repository.findAndLockUnsent(batchSize);
        if (events.isEmpty()) {
            return;
        }

        // ExecutorService с фиксированным числом потоков
        ExecutorService executor = Executors.newFixedThreadPool(threadPool);

        try {
            List<CompletableFuture<Void>> futures = events.stream()
                    // Каждая отправка в Kafka запускается через CompletableFuture.runAsync
                    .map(event -> CompletableFuture.runAsync(() -> {
                        try {
                            kafkaProducer.sendSync(topic, event.getPayload());
                            event.setSent(true);
                        } catch (Exception e) {
                            log.error("Failed to send event id={} payload={}", event.getId(), event.getPayload(), e);
                        }
                    }, executor))
                    .collect(Collectors.toList());

            // Ждём завершения всех задач
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            repository.saveAll(events);
        } finally {
            executor.shutdown(); // Пул потоков закрывается
        }
    }
}
