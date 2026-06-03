CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE sessions (
    id            BIGSERIAL PRIMARY KEY,
    client_id     UUID NOT NULL,
    scenario_name VARCHAR(200),
    status        VARCHAR(20) DEFAULT 'IN_PROGRESS',
    started_at    TIMESTAMPTZ DEFAULT now(),
    ended_at      TIMESTAMPTZ
);

CREATE TABLE vessel_logs (
    id           BIGSERIAL PRIMARY KEY,
    session_id   BIGINT REFERENCES sessions(id),
    recorded_at  TIMESTAMPTZ NOT NULL,
    position     GEOGRAPHY(POINT, 4326),
    speed_kn     FLOAT,
    heading_deg  FLOAT,
    rudder_angle FLOAT,
    throttle     FLOAT,
    roll         FLOAT,
    pitch        FLOAT,
    yaw          FLOAT
);
CREATE INDEX idx_vessel_logs_session ON vessel_logs(session_id);

CREATE TABLE environment_logs (
    id                BIGSERIAL PRIMARY KEY,
    session_id        BIGINT REFERENCES sessions(id),
    recorded_at       TIMESTAMPTZ NOT NULL,
    wind_speed        FLOAT,
    wind_direction    FLOAT,
    wave_height       FLOAT,
    current_speed     FLOAT,
    current_direction FLOAT,
    tide_level        FLOAT,
    visibility        FLOAT
);
CREATE INDEX idx_env_logs_session ON environment_logs(session_id);

CREATE TABLE event_logs (
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT REFERENCES sessions(id),
    event_type  VARCHAR(50),
    event_time  TIMESTAMPTZ,
    severity    VARCHAR(20),
    description TEXT,
    position    GEOGRAPHY(POINT, 4326)
);

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
