package com.safesail.collector.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "평가 리포트 응답")
public record ReportResponse(
        @Schema(description = "총점 (0~100)", example = "85.5") Double totalScore,
        @Schema(description = "충돌 발생 횟수", example = "0") Integer collisionCount,
        @Schema(description = "경로 이탈 횟수", example = "2") Integer routeDeviationCount,
        @Schema(description = "과속 횟수", example = "1") Integer speedingCount,
        @Schema(description = "좌초 위험 횟수", example = "0") Integer groundingRiskCount,
        @Schema(description = "합격 여부", example = "true") Boolean passed,
        @Schema(description = "피드백 메시지", example = "경로 이탈이 2회 발생했습니다.") String feedback,
        @Schema(description = "리포트 생성 시각", example = "2025-01-01T01:00:00Z") OffsetDateTime createdAt
) {}
