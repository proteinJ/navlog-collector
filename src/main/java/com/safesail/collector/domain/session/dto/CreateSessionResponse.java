package com.safesail.collector.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 생성 응답")
public record CreateSessionResponse(
        @Schema(description = "생성된 세션 ID", example = "1042") Long sessionId
) {}
