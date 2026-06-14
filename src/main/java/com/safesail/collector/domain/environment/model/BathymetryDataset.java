package com.safesail.collector.domain.environment.model;

import java.time.Instant;
import java.util.List;

public record BathymetryDataset(
        double originLat,
        double originLon,
        int radiusMeters,
        Instant loadedAt,
        List<UnityDepthPoint> points
) {
    public BathymetryDataset {
        points = List.copyOf(points);
    }
}

//전체 수심 데이터와 적재 시각 수심
