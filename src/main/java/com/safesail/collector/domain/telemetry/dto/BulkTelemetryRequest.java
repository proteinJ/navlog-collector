package com.safesail.collector.domain.telemetry.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "운항 기록 Bulk 전송 요청")
public record BulkTelemetryRequest(
        @Schema(description = "선박 운항 로그 목록") List<VesselLogDto> vesselLogs,
        @Schema(description = "환경 로그 목록") List<EnvironmentLogDto> environmentLogs,
        @Schema(description = "이벤트 목록") List<EventDto> events
) {
    @Schema(description = "선박 운항 로그")
    public record VesselLogDto(
            @Schema(description = "기록 시각 (UTC)", example = "2025-01-01T00:00:00Z") Instant recordedAt,
            @Schema(description = "위도", example = "35.1") Double latitude,
            @Schema(description = "경도", example = "129.0") Double longitude,
            @Schema(description = "속력 (knot)", example = "5.2") Double speedKn,
            @Schema(description = "선수 방위 (도)", example = "90.0") Double headingDeg,
            @Schema(description = "타각 (도, 우현+)", example = "-5.0") Double rudderAngle,
            @Schema(description = "스로틀 (0.0~1.0)", example = "0.6") Double throttle,
            @Schema(description = "롤 (도)", example = "1.2") Double roll,
            @Schema(description = "피치 (도)", example = "0.3") Double pitch,
            @Schema(description = "요 (도)", example = "90.0") Double yaw
    ) {}

    @Schema(description = "환경 로그")
    public record EnvironmentLogDto(
            @Schema(description = "기록 시각 (UTC)", example = "2025-01-01T00:00:00Z") Instant recordedAt,
            @Schema(description = "풍속 (m/s)", example = "5.0") Double windSpeed,
            @Schema(description = "풍향 (도)", example = "270.0") Double windDirection,
            @Schema(description = "파고 (m)", example = "1.5") Double waveHeight,
            @Schema(description = "조류 속도 (knot)", example = "0.8") Double currentSpeed,
            @Schema(description = "조류 방향 (도)", example = "180.0") Double currentDirection,
            @Schema(description = "조위 (cm)", example = "120.0") Double tideLevel,
            @Schema(description = "시정 (km)", example = "8.0") Double visibility
    ) {}

    @Schema(description = "이벤트")
    public record EventDto(
            @Schema(description = "이벤트 유형 (예: COLLISION_WARNING)", example = "COLLISION_WARNING") String eventType,
            @Schema(description = "이벤트 발생 시각 (UTC)", example = "2025-01-01T00:00:05Z") Instant eventTime,
            @Schema(description = "심각도 (HIGH / MEDIUM / LOW)", example = "HIGH") String severity,
            @Schema(description = "이벤트 설명", example = "전방 50m 암초 발견 경고") String description,
            @Schema(description = "위도", example = "35.1") Double latitude,
            @Schema(description = "경도", example = "129.0") Double longitude
    ) {}
}
