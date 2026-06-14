package com.safesail.collector.infra.depth;

import com.safesail.collector.domain.environment.model.ChartDepthPoint;
import com.safesail.collector.domain.environment.model.GeoBoundingBox;

import java.util.List;

public interface DepthApiClient {

    List<ChartDepthPoint> fetch(GeoBoundingBox boundingBox);
}
