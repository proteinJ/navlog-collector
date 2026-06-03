package com.safesail.collector.domain.session.controller;

import com.safesail.collector.domain.session.dto.CreateSessionRequest;
import com.safesail.collector.domain.session.dto.CreateSessionResponse;
import com.safesail.collector.domain.session.dto.EndSessionRequest;
import com.safesail.collector.domain.session.service.SessionService;
import com.safesail.collector.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Session", description = "훈련 세션 생성 및 종료")
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(
            summary = "세션 생성",
            description = "Unity 클라이언트가 훈련 시작 시 호출합니다. clientId는 클라이언트 최초 실행 시 생성한 UUID-v4를 사용합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CreateSessionResponse>> createSession(
            @RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.createSession(request)));
    }

    @Operation(
            summary = "세션 종료",
            description = "훈련이 끝나거나 중단될 때 호출합니다. status는 COMPLETED 또는 ABORTED 중 하나입니다."
    )
    @PatchMapping("/{sessionId}/end")
    public ResponseEntity<ApiResponse<Void>> endSession(
            @Parameter(description = "종료할 세션 ID", example = "1042") @PathVariable Long sessionId,
            @RequestBody EndSessionRequest request) {
        sessionService.endSession(sessionId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
