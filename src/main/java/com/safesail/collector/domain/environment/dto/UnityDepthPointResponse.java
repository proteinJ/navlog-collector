package com.safesail.collector.domain.environment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Unity 좌표계 수심 지점")
public record UnityDepthPointResponse(
        @Schema(description = "동서 방향 Unity X (m)", example = "120.5") double x,
        @Schema(description = "남북 방향 Unity Z (m)", example = "-340.2") double z,
        @Schema(description = "해도 기준 수심 (m)", example = "24.7") double depth
) {
}
