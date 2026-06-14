package com.safesail.collector.domain.environment.dto;

import com.safesail.collector.domain.environment.model.BathymetryDataset;
import com.safesail.collector.domain.environment.model.UnityDepthPoint;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BathymetryResponseTest {

    @Test
    void keepsChartDepthAndTideLevelSeparate() {
        BathymetryDataset dataset = new BathymetryDataset(
                34.7667,
                128.9000,
                5_000,
                Instant.now(),
                List.of(new UnityDepthPoint(0.0, 0.0, 20.0))
        );

        BathymetryResponse response = BathymetryResponse.from(dataset, 1.2);

        assertThat(response.depthPoints().getFirst().depth()).isEqualTo(20.0);
        assertThat(response.tideLevelMeter()).isEqualTo(1.2);
        assertThat(response.effectiveDepthFormula()).isEqualTo("depth + tideLevelMeter");
    }
}
