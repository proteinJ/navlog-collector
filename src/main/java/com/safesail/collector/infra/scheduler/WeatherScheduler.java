package com.safesail.collector.infra.scheduler;

import com.safesail.collector.infra.redis.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final CacheService cacheService;

    @Scheduled(cron = "0 0 * * * *")
    public void collectWeatherData() {
        Map<String, String> mockData = Map.of(
                "waveHeight", "1.5",
                "windSpeed", "5.0",
                "windDirection", "270.0",
                "visibility", "8.0",
                "tideLevel", "120"
        );
        cacheService.setWeatherCache(mockData);
    }
}
