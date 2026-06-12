package com.safesail.collector.infra.weather;

public record WeatherObservation(
        Double waveHeight,
        Double windSpeed,
        Double windDirection,
        Integer tideLevel
) {
}
/*
파고
풍속
풍향
풍속
조위
 */