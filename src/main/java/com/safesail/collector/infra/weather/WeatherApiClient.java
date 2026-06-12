package com.safesail.collector.infra.weather;

public interface WeatherApiClient {

    WeatherObservation fetch();
}
//fetch로 api 날씨 가져와서 옵져베이션 레코드 형태로 반환