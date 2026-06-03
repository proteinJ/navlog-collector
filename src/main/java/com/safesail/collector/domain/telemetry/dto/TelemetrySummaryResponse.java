package com.safesail.collector.domain.telemetry.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 로그 건수 요약")
public record TelemetrySummaryResponse(
        @Schema(description = "선박 로그 총 건수", example = "120") long vesselLogs,
        @Schema(description = "환경 로그 총 건수", example = "120") long environmentLogs,
        @Schema(description = "이벤트 총 건수", example = "5") long events
) {}
