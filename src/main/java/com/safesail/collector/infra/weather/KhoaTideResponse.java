package com.safesail.collector.infra.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KhoaTideResponse(
        Result result
) {
    public record Result(
            List<Data> data
    ){}

    public record Data(
        @JsonProperty("record_time")
        String recordTime,

        @JsonProperty("tide_level")
        String tideLevel
    ){}

}
