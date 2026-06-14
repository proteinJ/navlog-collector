package com.safesail.collector.domain.environment.service;

import com.safesail.collector.domain.environment.config.MarineSimulationProperties;
import com.safesail.collector.domain.environment.model.BathymetryDataset;
import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import com.safesail.collector.domain.environment.model.UnityDepthPoint;
import com.safesail.collector.infra.depth.BathymetryCacheService;
import com.safesail.collector.infra.depth.DepthApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BathymetryService {

    private final DepthApiClient depthApiClient;
    private final BathymetryCacheService cacheService;
    private final MarineCoordinateConverter coordinateConverter;
    private final MarineSimulationProperties properties;
    private final Object refreshLock = new Object();
    private final AtomicReference<Instant> nextRefreshAttemptAt = new AtomicReference<>(Instant.MIN);

    public BathymetryDataset getBathymetry() {
        Optional<BathymetryDataset> cached = currentAreaCache();
        if (cached.filter(this::isFresh).isPresent()) {
            return cached.get();
        }
        if (cached.isPresent() && refreshRetryDelayed()) {
            return cached.get();
        }

        synchronized (refreshLock) {
            cached = currentAreaCache();
            if (cached.filter(this::isFresh).isPresent()) {
                return cached.get();
            }
            if (cached.isPresent() && refreshRetryDelayed()) {
                return cached.get();
            }

            try {
                BathymetryDataset refreshed = fetchAndConvert();
                cacheService.put(refreshed);
                nextRefreshAttemptAt.set(Instant.MIN);
                return refreshed;
            } catch (Exception e) {
                if (cached.isPresent()) {
                    nextRefreshAttemptAt.set(Instant.now().plus(properties.depthRefreshRetryDelay()));
                    log.warn("수심 API 갱신 실패. 이전 캐시를 fallback으로 사용합니다.", e);
                    return cached.get();
                }
                throw new IllegalStateException("수심 API 조회 실패 및 사용 가능한 캐시 없음", e);
            }
        }
    }

    private BathymetryDataset fetchAndConvert() {
        GeoBoundingBox boundingBox = coordinateConverter.boundingBox(
                properties.originLat(),
                properties.originLon(),
                properties.radiusMeters()
        );

        List<UnityDepthPoint> points = depthApiClient.fetch(boundingBox).stream()
                .filter(this::isInsideSimulationArea)
                .map(point -> coordinateConverter.toUnity(
                        properties.originLat(),
                        properties.originLon(),
                        point))
                .toList();

        if (points.isEmpty()) {
            throw new IllegalStateException("시뮬레이션 반경 안의 수심 데이터가 없습니다.");
        }

        return new BathymetryDataset(
                properties.originLat(),
                properties.originLon(),
                properties.radiusMeters(),
                Instant.now(),
                points
        );
    }

    private Optional<BathymetryDataset> currentAreaCache() {
        return cacheService.get().filter(this::matchesCurrentArea);
    }

    private boolean matchesCurrentArea(BathymetryDataset dataset) {
        return Double.compare(dataset.originLat(), properties.originLat()) == 0
                && Double.compare(dataset.originLon(), properties.originLon()) == 0
                && dataset.radiusMeters() == properties.radiusMeters();
    }

    private boolean isFresh(BathymetryDataset dataset) {
        return dataset.loadedAt()
                .plus(properties.depthCacheTtl())
                .isAfter(Instant.now());
    }

    private boolean refreshRetryDelayed() {
        return nextRefreshAttemptAt.get().isAfter(Instant.now());
    }

    private boolean isInsideSimulationArea(ChartDepthPoint point) {
        return coordinateConverter.distanceMeters(
                properties.originLat(),
                properties.originLon(),
                point.latitude(),
                point.longitude()
        ) <= properties.radiusMeters();
    }
}
//필터링과 캐시
// 이 부분이 핵심임 확인 요망
//bbox 수심 데이터 조회
//실제 거리 <= 5000m 데이터만 필터링
//Unity 좌표 변환
//빈 결과가 기존 캐시를 덮어쓰지 않도록 방어
//최신 캐시가 있으면 API를 호출하지 않음
//API 실패 시 오래된 캐시를 fallback으로 반환
//API 실패 후 1시간 동안 추가 호출 방지