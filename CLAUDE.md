```
# SafeSail-Collector 백엔드 서버 구현

## 프로젝트 개요
해양 운항 시뮬레이터(Unity)에서 발생하는 운항 데이터를 수집·저장하는
ML 학습용 데이터 파이프라인 백엔드 서버입니다.
로그인/회원가입 없이 UUID 기반으로 클라이언트를 식별합니다.

## 기술 스택
- Java 21 (Eclipse Temurin)
- Spring Boot 3.x
- PostgreSQL 16 + PostGIS 3.4
- Redis 7
- Docker + Docker Compose
- Gradle

## 프로젝트 구조
다음 패키지 구조로 생성해주세요:

com.safesail.collector
├── domain
│   ├── environment      # 해양/기상 데이터
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   └── entity
│   ├── session          # 훈련 세션
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   └── entity
│   ├── telemetry        # 운항 로그 (VesselLog, EnvironmentLog, EventLog)
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   └── entity
│   └── report           # 평가 리포트
│       ├── controller
│       ├── service
│       ├── repository
│       └── entity
├── infra
│   ├── redis            # RedisConfig, CacheService
│   └── scheduler        # 기상 API 배치 수집 (1시간 주기)
└── global
    ├── config           # WebMvcConfig, CorsConfig
    ├── exception        # GlobalExceptionHandler
    └── response         # 공통 응답 포맷 ApiResponse<T>

## API 명세 구현

아래 명세대로 구현해주세요.

### 0. 헬스체크
GET /api/v1/health
- 응답: { "status": "ok", "timestamp": "..." }

### 1. 해양 환경 데이터
GET /api/v1/environment/marine?latitude={lat}&longitude={lon}
- Redis 캐시(weather:cache) 우선 조회
- 수동 오버라이드(weather:manual) 존재 시 해당 값 우선 반환
- 응답 스키마:
  {
    "waveHeight": 2.1,
    "windSpeed": 5.0,
    "windDirection": 270.0,
    "visibility": 5.5,
    "tideLevel": 120,
    "isManualOverride": false
  }

GET /api/v1/environment/marine/manual
- 현재 수동 오버라이드 값 조회

POST /api/v1/environment/marine/manual
- 요청: { "windSpeed": 15.0, "waveHeight": 3.5, "visibility": 1.0,
          "windDirection": 90.0, "tideLevel": 80, "enabled": true }
- Redis weather:manual 에 저장
- enabled: false 시 오버라이드 해제

### 2. 훈련 세션
POST /api/v1/sessions
- 요청:
  {
    "clientId": "uuid-v4",       // Unity 클라이언트가 최초 실행 시 생성·보관
    "scenarioName": "부산항 충돌회피 시나리오 A"
  }
- 응답: { "sessionId": 1042 }
- DB: sessions 테이블에 status = IN_PROGRESS 로 저장

PATCH /api/v1/sessions/{sessionId}/end
- 요청: { "status": "COMPLETED" }  // COMPLETED | ABORTED
- DB: ended_at, status 업데이트

### 3. 운항 기록 Bulk 적재
POST /api/v1/sessions/{sessionId}/telemetry
- Unity에서 일정 주기(예: 5초)로 묶어서 전송
- 요청 스키마 (BulkTelemetryRequest):
  {
    "vesselLogs": [
      {
        "recordedAt": "2025-01-01T00:00:00Z",
        "latitude": 35.1,
        "longitude": 129.0,
        "speedKn": 5.2,
        "headingDeg": 90.0,
        "rudderAngle": -5.0,
        "throttle": 0.6,
        "roll": 1.2,
        "pitch": 0.3,
        "yaw": 90.0
      }
    ],
    "environmentLogs": [
      {
        "recordedAt": "2025-01-01T00:00:00Z",
        "windSpeed": 5.0,
        "windDirection": 270.0,
        "waveHeight": 1.5,
        "currentSpeed": 0.8,
        "currentDirection": 180.0,
        "tideLevel": 120.0,
        "visibility": 8.0
      }
    ],
    "events": [
      {
        "eventType": "COLLISION_WARNING",
        "eventTime": "2025-01-01T00:00:05Z",
        "severity": "HIGH",
        "description": "전방 50m 암초 발견 경고",
        "latitude": 35.1,
        "longitude": 129.0
      }
    ]
  }
- vesselLogs, environmentLogs는 PostGIS GEOGRAPHY(POINT, 4326)로 좌표 저장
- 응답: { "inserted": { "vesselLogs": 10, "environmentLogs": 10, "events": 2 } }

GET /api/v1/sessions/{sessionId}/telemetry/summary
- 현재까지 적재된 로그 건수 반환 (디버깅용)
- 응답: { "vesselLogs": 120, "environmentLogs": 120, "events": 5 }

### 4. 평가 리포트
GET /api/v1/sessions/{sessionId}/report
- evaluation_results 테이블에서 조회
- 응답:
  {
    "totalScore": 85.5,
    "collisionCount": 0,
    "routeDeviationCount": 2,
    "speedingCount": 1,
    "groundingRiskCount": 0,
    "passed": true,
    "feedback": "경로 이탈이 2회 발생했습니다.",
    "createdAt": "2025-01-01T01:00:00Z"
  }

## DB 스키마

다음 테이블을 생성하는 SQL 마이그레이션 파일(Flyway)을 작성해주세요:

```sql
-- sessions
CREATE TABLE sessions (
  id           BIGSERIAL PRIMARY KEY,
  client_id    UUID NOT NULL,
  scenario_name VARCHAR(200),
  status       VARCHAR(20) DEFAULT 'IN_PROGRESS',
  started_at   TIMESTAMPTZ DEFAULT now(),
  ended_at     TIMESTAMPTZ
);

-- vessel_logs
CREATE TABLE vessel_logs (
  id          BIGSERIAL PRIMARY KEY,
  session_id  BIGINT REFERENCES sessions(id),
  recorded_at TIMESTAMPTZ NOT NULL,
  position    GEOGRAPHY(POINT, 4326),
  speed_kn    FLOAT,
  heading_deg FLOAT,
  rudder_angle FLOAT,
  throttle    FLOAT,
  roll        FLOAT,
  pitch       FLOAT,
  yaw         FLOAT
);
CREATE INDEX idx_vessel_logs_session ON vessel_logs(session_id);

-- environment_logs
CREATE TABLE environment_logs (
  id              BIGSERIAL PRIMARY KEY,
  session_id      BIGINT REFERENCES sessions(id),
  recorded_at     TIMESTAMPTZ NOT NULL,
  wind_speed      FLOAT,
  wind_direction  FLOAT,
  wave_height     FLOAT,
  current_speed   FLOAT,
  current_direction FLOAT,
  tide_level      FLOAT,
  visibility      FLOAT
);
CREATE INDEX idx_env_logs_session ON environment_logs(session_id);

-- event_logs
CREATE TABLE event_logs (
  id          BIGSERIAL PRIMARY KEY,
  session_id  BIGINT REFERENCES sessions(id),
  event_type  VARCHAR(50),
  event_time  TIMESTAMPTZ,
  severity    VARCHAR(20),
  description TEXT,
  position    GEOGRAPHY(POINT, 4326)
);

-- evaluation_results
CREATE TABLE evaluation_results (
  id                    BIGSERIAL PRIMARY KEY,
  session_id            BIGINT REFERENCES sessions(id) UNIQUE,
  total_score           FLOAT,
  collision_count       INT DEFAULT 0,
  route_deviation_count INT DEFAULT 0,
  speeding_count        INT DEFAULT 0,
  grounding_risk_count  INT DEFAULT 0,
  passed                BOOLEAN,
  feedback              TEXT,
  created_at            TIMESTAMPTZ DEFAULT now()
);
```

## Redis 키 설계
- weather:cache     → Hash, TTL 1시간 (배치 수집값)
- weather:manual    → Hash, TTL 없음 (수동 오버라이드)
- session:active:{clientId} → String (진행 중 sessionId), TTL 4시간

## Docker Compose
PostgreSQL 16 + PostGIS, Redis 7, Spring Boot 앱
3개 서비스로 구성된 docker-compose.yml 작성

## 공통 응답 포맷
모든 API는 아래 포맷으로 감싸주세요:
{
  "success": true,
  "data": { ... },
  "error": null
}

## 추가 요구사항
- CORS: 모든 Origin 허용 (Unity WebGL 및 로컬 개발 대응)
- 기상 스케줄러: @Scheduled(cron = "0 0 * * * *") 로 1시간마다 실행
  실제 API 호출 대신 우선 Mock 데이터 반환하는 구조로 구현
  (추후 실제 공공데이터 API로 교체 예정)
- Bulk 적재 시 saveAll() 사용해서 DB 왕복 최소화
- 패키지명: com.safesail.collector
- 빌드 도구: Gradle (Kotlin DSL)
```

