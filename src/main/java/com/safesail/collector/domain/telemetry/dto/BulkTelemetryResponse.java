package com.safesail.collector.domain.telemetry.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bulk 적재 결과")
public record BulkTelemetryResponse(
        @Schema(description = "적재된 레코드 수") Inserted inserted
) {
    @Schema(description = "적재 건수 상세")
    public record Inserted(
            @Schema(description = "선박 로그 적재 건수", example = "10") int vesselLogs,
            @Schema(description = "환경 로그 적재 건수", example = "10") int environmentLogs,
            @Schema(description = "이벤트 적재 건수", example = "2") int events
    ) {}
}
