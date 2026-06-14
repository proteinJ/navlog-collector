package com.safesail.collector.infra.depth;

import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class KhoaDepthApiClient implements DepthApiClient {

    private final RestClient restClient;
    private final DepthApiProperties properties;

    public KhoaDepthApiClient(RestClient.Builder builder, DepthApiProperties properties) {
        this.restClient = builder.build();
        this.properties = properties;
    }

    @Override
    public List<ChartDepthPoint> fetch(GeoBoundingBox boundingBox) {
        List<ChartDepthPoint> points = new ArrayList<>();
        int pageNumber = 1;
        int totalCount;

        do {
            KhoaDepthResponse response = fetchPage(boundingBox, pageNumber);
            validate(response);

            List<KhoaDepthResponse.Item> items = response.body().items() == null
                    ? List.of()
                    : response.body().items().item();
            if (items == null || items.isEmpty()) {
                break;
            }

            items.stream()
                    .map(item -> new ChartDepthPoint(item.lat(), item.lot(), item.dpwt()))
                    .forEach(points::add);

            totalCount = response.body().totalCount();
            pageNumber++;
        } while (points.size() < totalCount);

        return List.copyOf(points);
    }

    private KhoaDepthResponse fetchPage(GeoBoundingBox boundingBox, int pageNumber) {
        String uri = UriComponentsBuilder.fromUriString(properties.baseUrl())
                .queryParam("serviceKey", properties.apiKey())
                .queryParam("type", "json")
                .queryParam("ymin", boundingBox.minLatitude())
                .queryParam("ymax", boundingBox.maxLatitude())
                .queryParam("xmin", boundingBox.minLongitude())
                .queryParam("xmax", boundingBox.maxLongitude())
                .queryParam("pageNo", pageNumber)
                .queryParam("numOfRows", properties.pageSize())
                .build()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(KhoaDepthResponse.class);
    }

    private void validate(KhoaDepthResponse response) {
        if (response == null || response.header() == null) {
            throw new IllegalStateException("KHOA 수심 API 응답이 비어 있습니다.");
        }
        if (!"00".equals(response.header().resultCode()) || response.body() == null) {
            throw new IllegalStateException(
                    "KHOA 수심 API 오류: " + response.header().resultCode()
                            + " " + response.header().resultMsg());
        }
    }
}
//수심 API 조회
//bbox를 ymin, ymax, xmin, xmax 로 전달
//페이지 당 300개씩 조회
//totalCount까지 모든 페이즈 반복 조회
//API 오류 응답은 예외 처리
