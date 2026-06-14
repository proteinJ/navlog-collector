package com.safesail.collector.domain.environment.service;

import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import com.safesail.collector.domain.environment.model.UnityDepthPoint;
import org.springframework.stereotype.Component;

@Component
public class MarineCoordinateConverter {

    static final double EARTH_RADIUS_METERS = 6_371_000.0;
    private static final double API_COORDINATE_SCALE = 100_000.0;

    public GeoBoundingBox boundingBox(double originLat, double originLon, double radiusMeters) {
        double angularDistance = radiusMeters / EARTH_RADIUS_METERS;
        double latitudeDelta = Math.toDegrees(angularDistance);
        double longitudeDelta = Math.toDegrees(Math.asin(
                Math.sin(angularDistance) / Math.cos(Math.toRadians(originLat))));

        // KHOA depth API accepts at most five decimal places for bbox coordinates.
        return new GeoBoundingBox(
                roundDown(originLat - latitudeDelta),
                roundUp(originLat + latitudeDelta),
                roundDown(originLon - longitudeDelta),
                roundUp(originLon + longitudeDelta)
        );
    } // 바운딩 박스 5KM 반경 포함하게 값 수정
    //

    public double distanceMeters(
            double originLat,
            double originLon,
            double latitude,
            double longitude
    ) {
        double latitudeDelta = Math.toRadians(latitude - originLat);
        double longitudeDelta = Math.toRadians(longitude - originLon);
        double originLatitudeRadians = Math.toRadians(originLat);
        double latitudeRadians = Math.toRadians(latitude);

        double haversine = Math.pow(Math.sin(latitudeDelta / 2.0), 2)
                + Math.cos(originLatitudeRadians)
                * Math.cos(latitudeRadians)
                * Math.pow(Math.sin(longitudeDelta / 2.0), 2);

        return 2.0 * EARTH_RADIUS_METERS * Math.asin(Math.min(1.0, Math.sqrt(haversine)));
        // Haversine 공식 범위가 0<=t<=1이기 때문에 부동 소수점 이슈로 인한 roundUp, Down

    }

    public UnityDepthPoint toUnity(
            double originLat,
            double originLon,
            ChartDepthPoint point
    ) {
        double x = Math.toRadians(point.longitude() - originLon)
                * EARTH_RADIUS_METERS
                * Math.cos(Math.toRadians(originLat));
        double z = Math.toRadians(point.latitude() - originLat) * EARTH_RADIUS_METERS;

        return new UnityDepthPoint(x, z, point.depth());
    } // 위도 및 경도를 Unity 좌표로 변환
    // x = (longitude - originLongitude) × 지구 반지름 × cos(originLatitude)
    // z = (latitude - originLatitude) × 지구 반지름
    // 동쪽 경도 증가 시 x값 증가 , 북쪽 위도 증가 시 z값 증가
    // 거제도 부이 좌표를 현재 세팅값으로 잡았기 때문에 부이 좌표가 0,0 기준임
    private double roundDown(double value) {
        return Math.floor(value * API_COORDINATE_SCALE) / API_COORDINATE_SCALE;
    }

    private double roundUp(double value) {
        return Math.ceil(value * API_COORDINATE_SCALE) / API_COORDINATE_SCALE;
    }
}
