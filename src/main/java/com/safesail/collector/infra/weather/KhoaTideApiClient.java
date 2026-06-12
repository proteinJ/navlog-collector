package com.safesail.collector.infra.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@Component
public class KhoaTideApiClient implements TideApiClient{

    private final RestClient restClient;
    private final String baseUrl;
    private final String apiKey;

    public KhoaTideApiClient(
            RestClient.Builder builder,
            @Value("${khoa.base-url}") String baseUrl,
            @Value("${khoa.api-key}") String apiKey
    ) {
        this.restClient = builder.build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public Integer fetchTideLevel() {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ServiceKey", apiKey)
                .queryParam("ResultType", "json")
                .build()
                .toUriString();

        KhoaTideResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(KhoaTideResponse.class);

        if (response == null
                || response.result() == null
                || response.result().data() == null
                || response.result().data().isEmpty()) {
            throw new IllegalStateException("Khao Tide Data Not Found");
        }
        String tideLevel = response.result()
                .data()
                .getFirst()
                .tideLevel();

        return (int) Double.parseDouble(tideLevel);
    }

}
