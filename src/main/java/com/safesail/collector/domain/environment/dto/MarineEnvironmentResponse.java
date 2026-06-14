package com.safesail.collector.domain.environment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "해양 환경 데이터 응답")
public record MarineEnvironmentResponse(
        @Schema(description = "파고 (m)", example = "2.1") Double waveHeight,
        @Schema(description = "풍속 (m/s)", example = "5.0") Double windSpeed,
        @Schema(description = "풍향 (도, 0-360)", example = "270.0") Double windDirection,
        @Schema(description = "시정 (km)", example = "5.5") Double visibility,
        @Schema(description = "파향 (도, 0-360)",example = "270.0") Double waveDirection,
        @Schema(description = "파주기(second)", example = "4.5초") Double wavePeriod,
        @Schema(description = "조위 (cm)", example = "120") Integer tideLevel,
        @Schema(description = "수동 오버라이드 적용 여부", example = "false") boolean isManualOverride
) {}
