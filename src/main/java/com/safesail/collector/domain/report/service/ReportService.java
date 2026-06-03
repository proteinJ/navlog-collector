package com.safesail.collector.domain.report.service;

import com.safesail.collector.domain.report.dto.ReportResponse;
import com.safesail.collector.domain.report.entity.EvaluationResult;
import com.safesail.collector.domain.report.repository.EvaluationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EvaluationResultRepository evaluationResultRepository;

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long sessionId) {
        EvaluationResult result = evaluationResultRepository.findBySession_Id(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Report not found for session: " + sessionId));
        return new ReportResponse(
                result.getTotalScore(),
                result.getCollisionCount(),
                result.getRouteDeviationCount(),
                result.getSpeedingCount(),
                result.getGroundingRiskCount(),
                result.getPassed(),
                result.getFeedback(),
                result.getCreatedAt()
        );
    }
}
