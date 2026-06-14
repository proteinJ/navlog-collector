package com.safesail.collector.infra.depth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safesail.collector.domain.environment.model.BathymetryDataset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BathymetryCacheService {

    private static final String CACHE_KEY = "bathymetry:geoje:dataset:v1";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final AtomicReference<BathymetryDataset> localCache = new AtomicReference<>();

    public Optional<BathymetryDataset> get() {
        BathymetryDataset local = localCache.get();
        if (local != null) {
            return Optional.of(local);
        }

        try {
            String cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached == null) {
                return Optional.empty();
            }

            BathymetryDataset dataset = objectMapper.readValue(cached, BathymetryDataset.class);
            localCache.set(dataset);
            return Optional.of(dataset);
        } catch (Exception e) {
            log.warn("Redis 수심 캐시 조회 실패", e);
            return Optional.empty();
        }
    }

    public void put(BathymetryDataset dataset) {
        localCache.set(dataset);

        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(dataset));
        } catch (JsonProcessingException e) {
            log.warn("수심 데이터 직렬화 실패. 로컬 캐시만 사용합니다.", e);
        } catch (Exception e) {
            log.warn("Redis 수심 캐시 저장 실패. 로컬 캐시만 사용합니다.", e);
        }
    }
}
//로컬 메모리 캐시: 같은 서버 프로세스에서 Redis 호출 최소화
//Redis 캐시: 서버 재시작 후에도 수심 데이터 재사용
//Redis 데이터 자체에는 TTL을 걸지 않았음
// 갱신 시간이 지나도 삭제하지 않아 외부 API 장애 시 이전 데이터를 사용 가능하게 설정