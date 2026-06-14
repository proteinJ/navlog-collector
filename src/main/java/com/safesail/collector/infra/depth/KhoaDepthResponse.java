package com.safesail.collector.infra.depth;

import java.util.List;

public record KhoaDepthResponse(
        Header header,
        Body body
) {
    public record Header(
            String resultCode,
            String resultMsg
    ) {
    }

    public record Body(
            Items items,
            int pageNo,
            int numOfRows,
            int totalCount,
            String type
    ) {
    }

    public record Items(
            List<Item> item
    ) {
    }

    public record Item(
            double lat,
            double lot,
            double dpwt
    ) {
    }
}

//KHOA 응답의 lat,lot,dpwt 필드를 Java 객체로 변환

