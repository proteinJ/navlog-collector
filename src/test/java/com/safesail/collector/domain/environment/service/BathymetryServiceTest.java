package com.safesail.collector.domain.environment.service;

import com.safesail.collector.domain.environment.config.MarineSimulationProperties;
import com.safesail.collector.domain.environment.model.BathymetryDataset;
import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import com.safesail.collector.domain.environment.model.UnityDepthPoint;
import com.safesail.collector.infra.depth.BathymetryCacheService;
import com.safesail.collector.infra.depth.DepthApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BathymetryServiceTest {

    private static final double ORIGIN_LAT = 34.7667;
    private static final double ORIGIN_LON = 128.9000;

    private final DepthApiClient depthApiClient = mock(DepthApiClient.class);
    private final BathymetryCacheService cacheService = mock(BathymetryCacheService.class);
    private final MarineCoordinateConverter coordinateConverter = new MarineCoordinateConverter();
    private final MarineSimulationProperties properties = new MarineSimulationProperties(
            ORIGIN_LAT,
            ORIGIN_LON,
            5_000,
            Duration.ofDays(7),
            Duration.ofHours(1)
    );

    private BathymetryService service;

    @BeforeEach
    void setUp() {
        service = new BathymetryService(
                depthApiClient,
                cacheService,
                coordinateConverter,
                properties
        );
    }

    @Test
    void reusesFreshCacheWithoutCallingDepthApi() {
        BathymetryDataset cached = dataset(Instant.now(), List.of(
                new UnityDepthPoint(0.0, 0.0, 20.0)
        ));
        when(cacheService.get()).thenReturn(Optional.of(cached));

        BathymetryDataset result = service.getBathymetry();

        assertThat(result).isSameAs(cached);
        verify(depthApiClient, never()).fetch(any());
    }

    @Test
    void fetchesBboxThenKeepsOnlyPointsInsideCircularRadius() {
        when(cacheService.get()).thenReturn(Optional.empty());
        when(depthApiClient.fetch(any())).thenReturn(List.of(
                new ChartDepthPoint(ORIGIN_LAT, ORIGIN_LON, 20.0),
                new ChartDepthPoint(ORIGIN_LAT + 0.03, ORIGIN_LON, 30.0),
                new ChartDepthPoint(ORIGIN_LAT + 0.06, ORIGIN_LON, 40.0)
        ));

        BathymetryDataset result = service.getBathymetry();

        assertThat(result.points()).hasSize(2);
        assertThat(result.points().getFirst().x()).isZero();
        assertThat(result.points().getFirst().z()).isZero();
        assertThat(result.points()).extracting(UnityDepthPoint::depth)
                .containsExactly(20.0, 30.0);

        ArgumentCaptor<GeoBoundingBox> boundingBox = ArgumentCaptor.forClass(GeoBoundingBox.class);
        verify(depthApiClient).fetch(boundingBox.capture());
        assertThat(boundingBox.getValue().minLatitude()).isLessThan(ORIGIN_LAT);
        assertThat(boundingBox.getValue().maxLatitude()).isGreaterThan(ORIGIN_LAT);
        assertThat(boundingBox.getValue().minLongitude()).isLessThan(ORIGIN_LON);
        assertThat(boundingBox.getValue().maxLongitude()).isGreaterThan(ORIGIN_LON);
        verify(cacheService).put(result);
    }

    @Test
    void fallsBackToStaleCacheWhenDepthApiFails() {
        BathymetryDataset stale = dataset(Instant.now().minus(Duration.ofDays(8)), List.of(
                new UnityDepthPoint(10.0, 20.0, 30.0)
        ));
        when(cacheService.get()).thenReturn(Optional.of(stale));
        when(depthApiClient.fetch(any())).thenThrow(new IllegalStateException("API unavailable"));

        BathymetryDataset result = service.getBathymetry();
        BathymetryDataset retryResult = service.getBathymetry();

        assertThat(result).isSameAs(stale);
        assertThat(retryResult).isSameAs(stale);
        verify(depthApiClient).fetch(any());
        verify(cacheService, never()).put(any());
    }

    private BathymetryDataset dataset(Instant loadedAt, List<UnityDepthPoint> points) {
        return new BathymetryDataset(
                ORIGIN_LAT,
                ORIGIN_LON,
                5_000,
                loadedAt,
                points
        );
    }
}
