package com.safesail.collector.infra.scheduler;

import com.safesail.collector.infra.redis.CacheService;
import com.safesail.collector.infra.weather.TideApiClient;
import com.safesail.collector.infra.weather.WeatherApiClient;
import com.safesail.collector.infra.weather.WeatherObservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherApiClient weatherApiClient;
    private final TideApiClient tideApiClient;
    private final CacheService cacheService;

    @Scheduled(cron = "${weather.scheduler-cron}")
    public void collectWeatherData() {
        Map<String, String> updates = new HashMap<>();

        try {
            WeatherObservation weather = weatherApiClient.fetch();

            put(updates, "waveHeight", weather.waveHeight());
            put(updates, "windSpeed", weather.windSpeed());
            put(updates, "windDirection", weather.windDirection());
            put(updates, "waveDirection", weather.waveDirection());
            put(updates, "wavePeriod", weather.wavePeriod());

        } catch (Exception e) {
            log.warn("KMA API 호출 실패", e);
        }

        try {
            Integer tideLevel = tideApiClient.fetchTideLevel();
            put(updates, "tideLevel", tideLevel);
        } catch (Exception e) {
            log.warn("KHOA API 호출 실패", e);
        }

        cacheService.updateWeatherCache(updates);
    }

    private void put(Map<String, String> updates, String key, Object value) {
        if (value != null) {
            updates.put(key, value.toString());
        }
    }
}