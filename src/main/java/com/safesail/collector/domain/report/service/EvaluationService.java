package com.safesail.collector.domain.report.service;

import com.safesail.collector.domain.report.entity.EvaluationResult;
import com.safesail.collector.domain.report.repository.EvaluationResultRepository;
import com.safesail.collector.domain.session.entity.Session;
import com.safesail.collector.domain.telemetry.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private static final double PASS_SCORE      = 60.0;
    private static final int    PENALTY_COLLISION  = 20;
    private static final int    PENALTY_SPEEDING   = 5;
    private static final int    PENALTY_DEVIATION  = 10;
    private static final int    PENALTY_GROUNDING  = 15;

    private final EventLogRepository        eventLogRepository;
    private final EvaluationResultRepository evaluationResultRepository;

    @Transactional
    public void evaluate(Session session) {
        Long sessionId = session.getId();

        int collisionCount  = (int) eventLogRepository.countBySession_IdAndEventType(sessionId, "COLLISION_WARNING");
        int speedingCount   = (int) eventLogRepository.countBySession_IdAndEventType(sessionId, "SPEEDING");
        int deviationCount  = (int) eventLogRepository.countBySession_IdAndEventType(sessionId, "ROUTE_DEVIATION");
        int groundingCount  = (int) eventLogRepository.countBySession_IdAndEventType(sessionId, "GROUNDING_WARNING");

        double totalScore = Math.max(0, 100
                - (collisionCount * PENALTY_COLLISION)
                - (speedingCount  * PENALTY_SPEEDING)
                - (deviationCount * PENALTY_DEVIATION)
                - (groundingCount * PENALTY_GROUNDING));

        boolean passed = totalScore >= PASS_SCORE;

        EvaluationResult result = EvaluationResult.builder()
                .session(session)
                .collisionCount(collisionCount)
                .speedingCount(speedingCount)
                .routeDeviationCount(deviationCount)
                .groundingRiskCount(groundingCount)
                .totalScore(totalScore)
                .passed(passed)
                .feedback(buildFeedback(collisionCount, speedingCount, deviationCount, groundingCount, passed))
                .createdAt(OffsetDateTime.now())
                .build();

        evaluationResultRepository.save(result);
    }

    private String buildFeedback(int collision, int speeding, int deviation, int grounding, boolean passed) {
        if (collision == 0 && speeding == 0 && deviation == 0 && grounding == 0) {
            return "안전한 항해였습니다.";
        }

        StringBuilder sb = new StringBuilder();
        if (collision  > 0) sb.append(String.format("충돌 경고 %d회. ", collision));
        if (speeding   > 0) sb.append(String.format("과속 %d회. ", speeding));
        if (deviation  > 0) sb.append(String.format("항로 이탈 %d회. ", deviation));
        if (grounding  > 0) sb.append(String.format("좌초 위험 %d회. ", grounding));

        sb.append(passed ? "훈련을 통과했습니다." : "훈련 기준(60점)을 통과하지 못했습니다.");
        return sb.toString().trim();
    }
}
