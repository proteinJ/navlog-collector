package com.safesail.collector.infra.weather;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KhoaTideApiClientTest {

    @Test
    void fetchesLatestMeasuredTideLevel() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        KhoaTideApiClient client = new KhoaTideApiClient(
                builder,
                new KhoaTideApiProperties("test-key", "https://example.com/tide", "DT_0029")
        );

        server.expect(requestTo(startsWith(
                        "https://example.com/tide/GetSurveyTideLevelApiService")))
                .andExpect(queryParam("serviceKey", "test-key"))
                .andExpect(queryParam("type", "json"))
                .andExpect(queryParam("obsCode", "DT_0029"))
                .andExpect(queryParam("min", "10"))
                .andExpect(queryParam("numOfRows", "300"))
                .andRespond(withSuccess("""
                        {
                          "header": {"resultCode": "00", "resultMsg": "NORMAL_SERVICE"},
                          "body": {
                            "items": {"item": [
                              {"obsvtrNm": "거제도", "obsrvnDt": "2026-06-15 18:00", "bscTdlvHgt": 127.0},
                              {"obsvtrNm": "거제도", "obsrvnDt": "2026-06-15 18:10", "bscTdlvHgt": 132.0},
                              {"obsvtrNm": "거제도", "obsrvnDt": "2026-06-15 18:20", "bscTdlvHgt": null}
                            ]},
                            "pageNo": 1,
                            "numOfRows": 300,
                            "totalCount": 144
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.fetchTideLevel()).isEqualTo(132);
        server.verify();
    }

    @Test
    void rejectsApiErrorResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        KhoaTideApiClient client = new KhoaTideApiClient(
                builder,
                new KhoaTideApiProperties("test-key", "https://example.com/tide", "DT_0029")
        );

        server.expect(requestTo(startsWith(
                        "https://example.com/tide/GetSurveyTideLevelApiService")))
                .andRespond(withSuccess("""
                        {
                          "header": {"resultCode": "99", "resultMsg": "UNKNOWN_ERROR"}
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(client::fetchTideLevel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99 UNKNOWN_ERROR");
        server.verify();
    }
}
