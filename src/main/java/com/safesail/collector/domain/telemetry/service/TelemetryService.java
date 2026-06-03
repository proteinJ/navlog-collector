package com.safesail.collector.domain.telemetry.service;

import com.safesail.collector.domain.session.entity.Session;
import com.safesail.collector.domain.session.repository.SessionRepository;
import com.safesail.collector.domain.telemetry.dto.BulkTelemetryRequest;
import com.safesail.collector.domain.telemetry.dto.BulkTelemetryResponse;
import com.safesail.collector.domain.telemetry.dto.TelemetrySummaryResponse;
import com.safesail.collector.domain.telemetry.entity.EnvironmentLog;
import com.safesail.collector.domain.telemetry.entity.EventLog;
import com.safesail.collector.domain.telemetry.entity.VesselLog;
import com.safesail.collector.domain.telemetry.repository.EnvironmentLogRepository;
import com.safesail.collector.domain.telemetry.repository.EventLogRepository;
import com.safesail.collector.domain.telemetry.repository.VesselLogRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private static final GeometryFactory GEO_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private final SessionRepository sessionRepository;
    private final VesselLogRepository vesselLogRepository;
    private final EnvironmentLogRepository environmentLogRepository;
    private final EventLogRepository eventLogRepository;

    @Transactional
    public BulkTelemetryResponse saveBulk(Long sessionId, BulkTelemetryRequest request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));

        List<VesselLog> vesselLogs = request.vesselLogs().stream()
                .map(dto -> VesselLog.builder()
                        .session(session)
                        .recordedAt(OffsetDateTime.ofInstant(dto.recordedAt(), ZoneOffset.UTC))
                        .position(makePoint(dto.longitude(), dto.latitude()))
                        .speedKn(dto.speedKn())
                        .headingDeg(dto.headingDeg())
                        .rudderAngle(dto.rudderAngle())
                        .throttle(dto.throttle())
                        .roll(dto.roll())
                        .pitch(dto.pitch())
                        .yaw(dto.yaw())
                        .build())
                .toList();

        List<EnvironmentLog> envLogs = request.environmentLogs().stream()
                .map(dto -> EnvironmentLog.builder()
                        .session(session)
                        .recordedAt(OffsetDateTime.ofInstant(dto.recordedAt(), ZoneOffset.UTC))
                        .windSpeed(dto.windSpeed())
                        .windDirection(dto.windDirection())
                        .waveHeight(dto.waveHeight())
                        .currentSpeed(dto.currentSpeed())
                        .currentDirection(dto.currentDirection())
                        .tideLevel(dto.tideLevel())
                        .visibility(dto.visibility())
                        .build())
                .toList();

        List<EventLog> events = request.events().stream()
                .map(dto -> EventLog.builder()
                        .session(session)
                        .eventType(dto.eventType())
                        .eventTime(OffsetDateTime.ofInstant(dto.eventTime(), ZoneOffset.UTC))
                        .severity(dto.severity())
                        .description(dto.description())
                        .position(makePoint(dto.longitude(), dto.latitude()))
                        .build())
                .toList();

        vesselLogRepository.saveAll(vesselLogs);
        environmentLogRepository.saveAll(envLogs);
        eventLogRepository.saveAll(events);

        return new BulkTelemetryResponse(
                new BulkTelemetryResponse.Inserted(vesselLogs.size(), envLogs.size(), events.size()));
    }

    @Transactional(readOnly = true)
    public TelemetrySummaryResponse getSummary(Long sessionId) {
        return new TelemetrySummaryResponse(
                vesselLogRepository.countBySession_Id(sessionId),
                environmentLogRepository.countBySession_Id(sessionId),
                eventLogRepository.countBySession_Id(sessionId));
    }

    private Point makePoint(Double lon, Double lat) {
        if (lon == null || lat == null) return null;
        return GEO_FACTORY.createPoint(new Coordinate(lon, lat));
    }
}
