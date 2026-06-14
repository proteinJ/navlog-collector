package com.safesail.collector.domain.environment.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
// 기본 세팅 값 (거제 부이 기준 반지름 5키로미터) 한번 더 물리적인 검증임
@Validated
@ConfigurationProperties(prefix = "simulation.marine")
public record MarineSimulationProperties(
        // 위도, 경도 범위 고정
        @DecimalMin("-90.0") @DecimalMax("90.0") double originLat,
        @DecimalMin("-180.0") @DecimalMax("180.0") double originLon,
        //반지름 값 고정
        @Min(5000) @Max(5000) int radiusMeters,
        //수심 데이터 캐시 유지시간 설정
        @NotNull Duration depthCacheTtl,
        //수심 API 호출 실패 시 얼마 후 재시도 할 건지
        @NotNull Duration depthRefreshRetryDelay
) {
}
