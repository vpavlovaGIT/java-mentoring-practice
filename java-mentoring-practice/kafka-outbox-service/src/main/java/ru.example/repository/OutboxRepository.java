package ru.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.entity.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findBySentFalse();
}
