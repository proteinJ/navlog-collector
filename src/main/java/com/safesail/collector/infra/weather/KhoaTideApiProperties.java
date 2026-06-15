package com.safesail.collector.infra.weather;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "khoa")
public record KhoaTideApiProperties(
        @NotBlank String apiKey,
        @NotBlank String baseUrl,
        @NotBlank String obsCode
) {
}
