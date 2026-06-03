package com.safesail.collector.domain.telemetry.controller;

import com.safesail.collector.domain.telemetry.dto.BulkTelemetryRequest;
import com.safesail.collector.domain.telemetry.dto.BulkTelemetryResponse;
import com.safesail.collector.domain.telemetry.dto.TelemetrySummaryResponse;
import com.safesail.collector.domain.telemetry.service.TelemetryService;
import com.safesail.collector.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Telemetry", description = "운항 기록 Bulk 적재 및 조회")
@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @Operation(
            summary = "운항 기록 Bulk 적재",
            description = "Unity에서 일정 주기(예: 5초)마다 vesselLogs, environmentLogs, events를 묶어 전송합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<BulkTelemetryResponse>> saveBulk(
            @Parameter(description = "세션 ID", example = "1042") @PathVariable Long sessionId,
            @RequestBody BulkTelemetryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.saveBulk(sessionId, request)));
    }

    @Operation(
            summary = "운항 기록 요약 조회",
            description = "세션에 적재된 로그 건수를 반환합니다. 디버깅 용도입니다."
    )
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TelemetrySummaryResponse>> getSummary(
            @Parameter(description = "세션 ID", example = "1042") @PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(telemetryService.getSummary(sessionId)));
    }
}
