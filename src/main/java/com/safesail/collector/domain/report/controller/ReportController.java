package com.safesail.collector.domain.report.controller;

import com.safesail.collector.domain.report.dto.ReportResponse;
import com.safesail.collector.domain.report.service.ReportService;
import com.safesail.collector.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "훈련 세션 평가 리포트 조회")
@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "평가 리포트 조회",
            description = "세션 종료 후 생성된 평가 결과를 조회합니다. 리포트가 없으면 404를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(
            @Parameter(description = "세션 ID", example = "1042") @PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReport(sessionId)));
    }
}
