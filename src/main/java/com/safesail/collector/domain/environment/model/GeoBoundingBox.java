package com.safesail.collector.domain.environment.model;

public record GeoBoundingBox(
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude
) {
}
// API 요청용 바운딩 박스