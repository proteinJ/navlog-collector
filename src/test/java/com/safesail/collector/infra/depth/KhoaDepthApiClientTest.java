package com.safesail.collector.infra.depth;

import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KhoaDepthApiClientTest {

    @Test
    void fetchesEveryPageInBbox() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        KhoaDepthApiClient client = new KhoaDepthApiClient(
                builder,
                new DepthApiProperties("test-key", "https://example.com/depth", 2)
        );

        server.expect(queryParam("pageNo", "1"))
                .andExpect(queryParam("numOfRows", "2"))
                .andRespond(withSuccess("""
                        {
                          "header": {"resultCode": "00", "resultMsg": "NORMAL_SERVICE"},
                          "body": {
                            "items": {"item": [
                              {"lat": 34.76, "lot": 128.89, "dpwt": 10.0},
                              {"lat": 34.77, "lot": 128.90, "dpwt": 20.0}
                            ]},
                            "pageNo": 1,
                            "numOfRows": 2,
                            "totalCount": 3,
                            "type": "json"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(queryParam("pageNo", "2"))
                .andExpect(queryParam("numOfRows", "2"))
                .andRespond(withSuccess("""
                        {
                          "header": {"resultCode": "00", "resultMsg": "NORMAL_SERVICE"},
                          "body": {
                            "items": {"item": [
                              {"lat": 34.78, "lot": 128.91, "dpwt": 30.0}
                            ]},
                            "pageNo": 2,
                            "numOfRows": 2,
                            "totalCount": 3,
                            "type": "json"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        List<ChartDepthPoint> result = client.fetch(
                new GeoBoundingBox(34.72, 34.82, 128.84, 128.96)
        );

        assertThat(result).hasSize(3);
        assertThat(result).extracting(ChartDepthPoint::depth)
                .containsExactly(10.0, 20.0, 30.0);
        server.verify();
    }
}
