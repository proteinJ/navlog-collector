package com.safesail.collector.domain.environment.service;

import com.safesail.collector.domain.environment.dto.ManualOverrideRequest;
import com.safesail.collector.domain.environment.dto.MarineEnvironmentResponse;
import com.safesail.collector.infra.redis.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnvironmentService {

    private final CacheService cacheService;

    public MarineEnvironmentResponse getMarineEnvironment(double latitude, double longitude) {
        Map<Object, Object> manual = cacheService.getManualOverride();
        if (!manual.isEmpty() && "true".equals(manual.get("enabled"))) {
            return toResponse(manual, true);
        }
        Map<Object, Object> cache = cacheService.getWeatherCache();
        if (!cache.isEmpty()) {
            return toResponse(cache, false);
        }
        throw new IllegalStateException("사용 가능한 해양 환경 관측 데이터가 없습니다.");
    }

    public MarineEnvironmentResponse getManualOverride() {
        Map<Object, Object> manual = cacheService.getManualOverride();
        if (manual.isEmpty()) return null;
        return toResponse(manual, true);
    }

    public void setManualOverride(ManualOverrideRequest request) {
        if (!request.enabled()) {
            cacheService.deleteManualOverride();
            return;
        }
        Map<String, String> data = Map.of(
                "windSpeed", String.valueOf(request.windSpeed()),
                "waveHeight", String.valueOf(request.waveHeight()),
                "visibility", String.valueOf(request.visibility()),
                "windDirection", String.valueOf(request.windDirection()),
                "waveDirection", String.valueOf(request.waveDirection()),
                "wavePeriod", String.valueOf(request.wavePeriod()),
                "tideLevel", String.valueOf(request.tideLevel()),
                "enabled", "true"

        );
        cacheService.setManualOverride(data);
    }

    private MarineEnvironmentResponse toResponse(Map<Object, Object> data, boolean isManual) {
        return new MarineEnvironmentResponse(
                parseDouble(data, "waveHeight"),
                parseDouble(data, "windSpeed"),
                parseDouble(data, "windDirection"),
                parseDouble(data, "visibility"),
                parseDouble(data, "waveDirection"),
                parseDouble(data, "wavePeriod"),
                parseInt(data, "tideLevel"),
                isManual
        );
    }

    private Double parseDouble(Map<Object, Object> data, String key) {
        Object val = data.get(key);
        return val != null ? Double.parseDouble(val.toString()) : null;
    }

    private Integer parseInt(Map<Object, Object> data, String key) {
        Object val = data.get(key);
        return val != null ? (int) Double.parseDouble(val.toString()) : null;
    }
}
