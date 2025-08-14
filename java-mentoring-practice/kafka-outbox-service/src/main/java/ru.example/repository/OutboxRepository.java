package ru.example.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import ru.example.entity.OutboxEvent;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    // добавлена аннотация @Lock(LockModeType.PESSIMISTIC_WRITE) - устанавливает пессимистичную блокировку для выбранных записей БД
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEvent e WHERE e.sent = false ORDER BY e.createdAt ASC")
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "0")) // SKIP LOCKED
    List<OutboxEvent> findTopUnsent(Pageable pageable);
}
