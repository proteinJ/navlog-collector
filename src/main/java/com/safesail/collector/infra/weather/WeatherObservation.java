package com.safesail.collector.infra.weather;

public record WeatherObservation(
        Double waveHeight,
        Double windSpeed,
        Double windDirection,
        Double waveDirection,
        Double wavePeriod
) {
}
/*
파고
풍속
풍향
파향(파도 방향)
파도 주기
 */