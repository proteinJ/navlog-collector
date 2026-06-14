package com.safesail.collector.infra.weather;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;




@Component
public class KmaWeatherApiClient implements WeatherApiClient{

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;

    public KmaWeatherApiClient(
            RestClient.Builder builder,
            @Value("${kma.base-url}") String apiUrl,
            @Value("${kma.service-key}") String apiKey
    ){
        this.restClient = builder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public WeatherObservation fetch() {
        String currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

        String uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("tm", currentTime)
                .queryParam("stn",0)
                .queryParam("help",1)
                .queryParam("authKey", apiKey)
                .build()
                .toUriString();

        String response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        System.out.println(response);

        String dataLine = response.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !line.contains("-99"))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("KMA 정상 관측자료 없음"));

        String[] values = dataLine.split("\\s+");

        Double windDirection = Double.parseDouble(values[2]);
        Double windSpeed = Double.parseDouble(values[3]);
        Double waveHeight = Double.parseDouble(values[13]);

        return new WeatherObservation(
                waveHeight,
                windSpeed,
                windDirection
        ); // tideLevel을 따로 관리하고 있어 제거

    }

}
