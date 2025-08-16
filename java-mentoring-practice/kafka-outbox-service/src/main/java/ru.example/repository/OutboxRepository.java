package ru.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.entity.OutboxEvent;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(
            value = """
            SELECT * FROM outbox_event
            WHERE sent = false
            ORDER BY created_at ASC
            FOR UPDATE SKIP LOCKED
            LIMIT :limit
            """,
            nativeQuery = true
    )
    List<OutboxEvent> findAndLockUnsent(@Param("limit") int limit);
}
