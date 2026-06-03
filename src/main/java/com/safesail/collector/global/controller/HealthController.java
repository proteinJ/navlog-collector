package com.safesail.collector.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@Tag(name = "Health", description = "서버 상태 확인")
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @Operation(summary = "헬스체크", description = "서버 정상 동작 여부를 확인합니다.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "timestamp", OffsetDateTime.now().toString()
        ));
    }
}
