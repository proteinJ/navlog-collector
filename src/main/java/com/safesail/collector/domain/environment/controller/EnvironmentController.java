package com.safesail.collector.domain.environment.controller;

import com.safesail.collector.domain.environment.config.MarineSimulationProperties;
import com.safesail.collector.domain.environment.dto.BathymetryResponse;
import com.safesail.collector.domain.environment.dto.ManualOverrideRequest;
import com.safesail.collector.domain.environment.dto.MarineEnvironmentResponse;
import com.safesail.collector.domain.environment.model.BathymetryDataset;
import com.safesail.collector.domain.environment.service.BathymetryService;
import com.safesail.collector.domain.environment.service.EnvironmentService;
import com.safesail.collector.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Environment", description = "해양 환경 데이터 조회 및 수동 오버라이드")
@RestController
@RequestMapping("/api/v1/environment")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;
    private final BathymetryService bathymetryService;
    private final MarineSimulationProperties simulationProperties;

    @Operation(
            summary = "해양 환경 데이터 조회",
            description = "지정 좌표의 해양 환경 데이터를 반환합니다. 수동 오버라이드 > Redis 캐시 > 기본값 순으로 조회합니다."
    )
    @GetMapping("/marine")
    public ResponseEntity<ApiResponse<MarineEnvironmentResponse>> getMarineEnvironment(
            @Parameter(description = "위도 (예: 35.1)", example = "35.1") @RequestParam double latitude,
            @Parameter(description = "경도 (예: 129.0)", example = "129.0") @RequestParam double longitude) {
        return ResponseEntity.ok(ApiResponse.ok(
                environmentService.getMarineEnvironment(latitude, longitude)));
    }

    @Operation(
            summary = "거제도 부이 중심 수심 데이터 조회",
            description = "거제도 부이를 Unity 원점으로 변환한 반경 5km 수심 데이터를 반환합니다. "
                    + "실효 수심은 depth + tideLevelMeter로 계산합니다."
    )
    @GetMapping("/bathymetry")
    public ResponseEntity<ApiResponse<BathymetryResponse>> getBathymetry() {
        BathymetryDataset bathymetry = bathymetryService.getBathymetry();
        Integer tideLevelCentimeter = environmentService.getMarineEnvironment(
                simulationProperties.originLat(),
                simulationProperties.originLon()
        ).tideLevel();
        Double tideLevelMeter = tideLevelCentimeter == null ? null : tideLevelCentimeter / 100.0;

        return ResponseEntity.ok(ApiResponse.ok(BathymetryResponse.from(
                bathymetry,
                tideLevelMeter
        )));
    }

    @Operation(summary = "수동 오버라이드 조회", description = "현재 설정된 수동 오버라이드 값을 반환합니다.")
    @GetMapping("/marine/manual")
    public ResponseEntity<ApiResponse<MarineEnvironmentResponse>> getManualOverride() {
        return ResponseEntity.ok(ApiResponse.ok(environmentService.getManualOverride()));
    }

    @Operation(
            summary = "수동 오버라이드 설정",
            description = "해양 환경 데이터를 수동으로 고정합니다. enabled=false 시 오버라이드를 해제합니다."
    )
    @PostMapping("/marine/manual")
    public ResponseEntity<ApiResponse<Void>> setManualOverride(
            @RequestBody ManualOverrideRequest request) {
        environmentService.setManualOverride(request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
//API 응답 부분에 아래 엔드포인트 추가했습니다.
//GET /api/v1/environment/bathymetry