package com.safesail.collector.domain.telemetry.repository;

import com.safesail.collector.domain.telemetry.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    long countBySession_Id(Long sessionId);
    long countBySession_IdAndEventType(Long sessionId, String eventType);
}
