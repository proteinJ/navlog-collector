package com.safesail.collector.domain.environment.model;

public record ChartDepthPoint(
        double latitude,
        double longitude,
        double depth
) {
}
//원본 위도/경도/수심