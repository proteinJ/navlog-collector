package com.safesail.collector.domain.environment.service;

import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import com.safesail.collector.domain.environment.model.UnityDepthPoint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarineCoordinateConverterTest {

    private static final double ORIGIN_LAT = 34.7667;
    private static final double ORIGIN_LON = 128.9000;
    private static final double RADIUS_METERS = 5_000.0;

    private final MarineCoordinateConverter converter = new MarineCoordinateConverter();

    @Test
    void buoyPositionBecomesUnityOrigin() {
        UnityDepthPoint point = converter.toUnity(
                ORIGIN_LAT,
                ORIGIN_LON,
                new ChartDepthPoint(ORIGIN_LAT, ORIGIN_LON, 25.5)
        );

        assertThat(point.x()).isZero();
        assertThat(point.z()).isZero();
        assertThat(point.depth()).isEqualTo(25.5);
    }

    @Test
    void longitudeMapsToXAndLatitudeMapsToZ() {
        UnityDepthPoint east = converter.toUnity(
                ORIGIN_LAT,
                ORIGIN_LON,
                new ChartDepthPoint(ORIGIN_LAT, ORIGIN_LON + 0.01, 10.0)
        );
        UnityDepthPoint north = converter.toUnity(
                ORIGIN_LAT,
                ORIGIN_LON,
                new ChartDepthPoint(ORIGIN_LAT + 0.01, ORIGIN_LON, 10.0)
        );

        assertThat(east.x()).isPositive();
        assertThat(east.z()).isZero();
        assertThat(north.x()).isZero();
        assertThat(north.z()).isPositive();
    }

    @Test
    void boundingBoxContainsAllCardinalPointsAtFiveKilometers() {
        GeoBoundingBox boundingBox = converter.boundingBox(
                ORIGIN_LAT,
                ORIGIN_LON,
                RADIUS_METERS
        );

        for (double bearing : new double[]{0.0, 90.0, 180.0, 270.0}) {
            double[] destination = destinationPoint(
                    ORIGIN_LAT,
                    ORIGIN_LON,
                    RADIUS_METERS,
                    bearing
            );

            assertThat(destination[0]).isBetween(
                    boundingBox.minLatitude(),
                    boundingBox.maxLatitude()
            );
            assertThat(destination[1]).isBetween(
                    boundingBox.minLongitude(),
                    boundingBox.maxLongitude()
            );
        }

        assertThat(boundingBox.minLatitude()).isEqualTo(34.72173);
        assertThat(boundingBox.maxLatitude()).isEqualTo(34.81167);
    }

    @Test
    void distanceUsesCircularRadius() {
        double[] inside = destinationPoint(ORIGIN_LAT, ORIGIN_LON, 4_999.0, 45.0);
        double[] outside = destinationPoint(ORIGIN_LAT, ORIGIN_LON, 5_001.0, 45.0);

        assertThat(converter.distanceMeters(ORIGIN_LAT, ORIGIN_LON, inside[0], inside[1]))
                .isLessThanOrEqualTo(RADIUS_METERS);
        assertThat(converter.distanceMeters(ORIGIN_LAT, ORIGIN_LON, outside[0], outside[1]))
                .isGreaterThan(RADIUS_METERS);
    }

    private double[] destinationPoint(
            double latitude,
            double longitude,
            double distanceMeters,
            double bearingDegrees
    ) {
        double angularDistance = distanceMeters / MarineCoordinateConverter.EARTH_RADIUS_METERS;
        double bearing = Math.toRadians(bearingDegrees);
        double latitudeRadians = Math.toRadians(latitude);
        double longitudeRadians = Math.toRadians(longitude);

        double destinationLatitude = Math.asin(
                Math.sin(latitudeRadians) * Math.cos(angularDistance)
                        + Math.cos(latitudeRadians) * Math.sin(angularDistance) * Math.cos(bearing)
        );
        double destinationLongitude = longitudeRadians + Math.atan2(
                Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(latitudeRadians),
                Math.cos(angularDistance) - Math.sin(latitudeRadians) * Math.sin(destinationLatitude)
        );

        return new double[]{
                Math.toDegrees(destinationLatitude),
                Math.toDegrees(destinationLongitude)
        };
    }
}
