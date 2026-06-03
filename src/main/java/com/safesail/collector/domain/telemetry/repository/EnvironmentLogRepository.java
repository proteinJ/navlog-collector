package com.safesail.collector.domain.telemetry.repository;

import com.safesail.collector.domain.telemetry.entity.EnvironmentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentLogRepository extends JpaRepository<EnvironmentLog, Long> {
    long countBySession_Id(Long sessionId);
}
