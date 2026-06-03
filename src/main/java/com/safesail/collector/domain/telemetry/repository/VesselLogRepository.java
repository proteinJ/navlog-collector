package com.safesail.collector.domain.telemetry.repository;

import com.safesail.collector.domain.telemetry.entity.VesselLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VesselLogRepository extends JpaRepository<VesselLog, Long> {
    long countBySession_Id(Long sessionId);
}
