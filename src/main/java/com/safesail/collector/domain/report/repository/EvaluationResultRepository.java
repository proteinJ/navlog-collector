package com.safesail.collector.domain.report.repository;

import com.safesail.collector.domain.report.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    Optional<EvaluationResult> findBySession_Id(Long sessionId);
}
