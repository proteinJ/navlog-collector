package com.safesail.collector.infra.weather;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class WeatherApiConfig {

    @Bean
    public RestClientCustomizer weatherRestClientCustomizer(){
        return builder -> {
                SimpleClientHttpRequestFactory requestFactory =
                        new SimpleClientHttpRequestFactory();

                requestFactory.setConnectTimeout(Duration.ofSeconds(3));
                requestFactory.setReadTimeout(Duration.ofSeconds(5));

                builder.requestFactory(requestFactory);

        };
    }
}
