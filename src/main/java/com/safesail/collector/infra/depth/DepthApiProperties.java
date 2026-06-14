package com.safesail.collector.infra.depth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "depth")
public record DepthApiProperties(
        @NotBlank String apiKey,
        @NotBlank String baseUrl,
        @Min(1) @Max(300) int pageSize
) {
}
