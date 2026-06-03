package com.safesail.collector.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SafeSail Collector API")
                        .description("해양 운항 시뮬레이터(Unity) 데이터 수집 백엔드 서버")
                        .version("v1"));
    }
}
