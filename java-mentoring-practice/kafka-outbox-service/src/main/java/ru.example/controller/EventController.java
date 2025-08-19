package ru.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.entity.OutboxEvent;
import ru.example.repository.OutboxRepository;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final OutboxRepository repository;

    public EventController(OutboxRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<String> receiveEvent(@RequestBody String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setPayload(payload);
        repository.save(event);
        return ResponseEntity.ok("Event received and saved to outbox.");
    }
}
