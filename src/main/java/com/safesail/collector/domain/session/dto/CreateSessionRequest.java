package com.safesail.collector.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "세션 생성 요청")
public record CreateSessionRequest(
        @Schema(description = "Unity 클라이언트 UUID (최초 실행 시 생성 후 보관)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID clientId,
        @Schema(description = "시나리오 이름", example = "부산항 충돌회피 시나리오 A")
        String scenarioName
) {}
