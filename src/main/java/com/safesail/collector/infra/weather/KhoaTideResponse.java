package com.safesail.collector.infra.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KhoaTideResponse(
        Header header,
        Body body
) {
    public record Header(
            String resultCode,
            String resultMsg
    ) {
    }

    public record Body(
            Items items
    ) {
    }

    public record Items(
            List<Item> item
    ) {
    }

    public record Item(
            @JsonProperty("obsrvnDt")
            String observedAt,

            @JsonProperty("bscTdlvHgt")
            Double tideLevel
    ) {
    }
}
