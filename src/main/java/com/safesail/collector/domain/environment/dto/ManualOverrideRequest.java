package com.safesail.collector.domain.environment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수동 오버라이드 설정 요청")
public record ManualOverrideRequest(
        @Schema(description = "풍속 (m/s)", example = "15.0") Double windSpeed,
        @Schema(description = "파고 (m)", example = "3.5") Double waveHeight,
        @Schema(description = "시정 (km)", example = "1.0") Double visibility,
        @Schema(description = "풍향 (도, 0-360)", example = "90.0") Double windDirection,
        @Schema(description = "파향 (도, 0-360)", example = "90.0") Double waveDirection,
        @Schema(description = "파주기 (Second)", example = "4.5") Double wavePeriod,
        @Schema(description = "조위 (cm)", example = "80") Integer tideLevel,
        @Schema(description = "오버라이드 활성화 여부. false 시 오버라이드 해제", example = "true") boolean enabled
) {}
