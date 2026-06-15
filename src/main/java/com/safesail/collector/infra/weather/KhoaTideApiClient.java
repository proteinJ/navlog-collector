package com.safesail.collector.infra.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;

@Component
public class KhoaTideApiClient implements TideApiClient {

    private static final String API_PATH = "/GetSurveyTideLevelApiService";
    private static final int OBSERVATION_INTERVAL_MINUTES = 10;
    private static final int MAX_ROWS = 300;

    private final RestClient restClient;
    private final KhoaTideApiProperties properties;

    public KhoaTideApiClient(RestClient.Builder builder, KhoaTideApiProperties properties) {
        this.restClient = builder.build();
        this.properties = properties;
    }

    @Override
    public Integer fetchTideLevel() {
        String uri = UriComponentsBuilder.fromUriString(properties.baseUrl())
                .path(API_PATH)
                .queryParam("serviceKey", properties.apiKey())
                .queryParam("type", "json")
                .queryParam("obsCode", properties.obsCode())
                .queryParam("min", OBSERVATION_INTERVAL_MINUTES)
                .queryParam("numOfRows", MAX_ROWS)
                .build()
                .toUriString();

        KhoaTideResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(KhoaTideResponse.class);

        validate(response);

        return response.body()
                .items()
                .item()
                .stream()
                .filter(item -> item.observedAt() != null && item.tideLevel() != null)
                .max(Comparator.comparing(KhoaTideResponse.Item::observedAt))
                .map(item -> item.tideLevel().intValue())
                .orElseThrow(() -> new IllegalStateException("KHOA 최신 실측 조위가 없습니다."));
    }

    private void validate(KhoaTideResponse response) {
        if (response == null || response.header() == null) {
            throw new IllegalStateException("KHOA 조위 API 응답이 비어 있습니다.");
        }

        if (!"00".equals(response.header().resultCode())) {
            throw new IllegalStateException(
                    "KHOA 조위 API 오류: " + response.header().resultCode()
                            + " " + response.header().resultMsg());
        }

        if (response.body() == null
                || response.body().items() == null
                || response.body().items().item() == null
                || response.body().items().item().isEmpty()) {
            throw new IllegalStateException("KHOA 조위 관측자료가 없습니다.");
        }
    }
}
