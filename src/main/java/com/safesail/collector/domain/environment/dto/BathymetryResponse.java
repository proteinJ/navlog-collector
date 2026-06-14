package com.safesail.collector.domain.environment.dto;

import com.safesail.collector.domain.environment.model.BathymetryDataset;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "거제도 부이 중심 Unity 수심 데이터")
public record BathymetryResponse(
        @Schema(description = "Unity 원점의 위도", example = "34.7667") double originLat,
        @Schema(description = "Unity 원점의 경도", example = "128.9") double originLon,
        @Schema(description = "유효 시뮬레이션 반경 (m)", example = "5000") int radiusMeters,
        @Schema(description = "수심 데이터 적재 시각") Instant depthLoadedAt,
        @Schema(description = "현재 조위 (m). depth와 별도로 관리됩니다.", example = "1.2") Double tideLevelMeter,
        @Schema(description = "실효 수심 계산식", example = "depth + tideLevelMeter")
        String effectiveDepthFormula,
        @Schema(description = "반경 5km 안의 Unity 좌표계 수심 목록")
        List<UnityDepthPointResponse> depthPoints
) {
    public static BathymetryResponse from(BathymetryDataset dataset, Double tideLevelMeter) {
        List<UnityDepthPointResponse> points = dataset.points().stream()
                .map(point -> new UnityDepthPointResponse(point.x(), point.z(), point.depth()))
                .toList();

        return new BathymetryResponse(
                dataset.originLat(),
                dataset.originLon(),
                dataset.radiusMeters(),
                dataset.loadedAt(),
                tideLevelMeter,
                "depth + tideLevelMeter",
                points
        );
    }
}

//API 응답 예시
//{
//  "originLat": 34.7667,
//  "originLon": 128.9,
//  "radiusMeters": 5000,
//  "depthLoadedAt": "2026-06-14T12:00:00Z",
//  "tideLevelMeter": 1.2,
//  "effectiveDepthFormula": "depth + tideLevelMeter",
//  "depthPoints": [
//    {
//      "x": 120.5,
//      "z": -340.2,
//      "depth": 24.7
//    }
//  ]
//}
//depth는 해도 기준 수심이고 tideLevelMeter는 현재 조위입니다.
// 두 값은 섞지 않고 별도로 반환합니다.
// Unity에서는 필요한 시점에 다음과 같이 계산할 수 있습니다.
//effectiveDepth = depth + tideLevelMeter