package com.safesail.collector.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String WEATHER_CACHE_KEY = "weather:cache";
    private static final String WEATHER_MANUAL_KEY = "weather:manual";
    private static final String SESSION_ACTIVE_PREFIX = "session:active:";

    private final StringRedisTemplate redisTemplate;

    public Map<Object, Object> getWeatherCache() {
        return redisTemplate.opsForHash().entries(WEATHER_CACHE_KEY);
    }

    public Map<Object, Object> getManualOverride() {
        return redisTemplate.opsForHash().entries(WEATHER_MANUAL_KEY);
    }

    public void setWeatherCache(Map<String, String> data) {
        redisTemplate.opsForHash().putAll(WEATHER_CACHE_KEY, data);
        redisTemplate.expire(WEATHER_CACHE_KEY, 1, TimeUnit.HOURS);
    }

    public void setManualOverride(Map<String, String> data) {
        redisTemplate.delete(WEATHER_MANUAL_KEY);
        redisTemplate.opsForHash().putAll(WEATHER_MANUAL_KEY, data);
    }

    public void deleteManualOverride() {
        redisTemplate.delete(WEATHER_MANUAL_KEY);
    }

    public void setSessionActive(UUID clientId, Long sessionId) {
        redisTemplate.opsForValue().set(
                SESSION_ACTIVE_PREFIX + clientId,
                String.valueOf(sessionId),
                4, TimeUnit.HOURS
        );
    }
}
