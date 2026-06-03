package com.safesail.collector.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 종료 요청")
public record EndSessionRequest(
        @Schema(description = "종료 상태. COMPLETED(정상 완료) 또는 ABORTED(중단)", example = "COMPLETED")
        String status
) {}
