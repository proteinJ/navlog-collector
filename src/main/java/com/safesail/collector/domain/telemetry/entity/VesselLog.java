package com.safesail.collector.domain.telemetry.entity;

import com.safesail.collector.domain.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vessel_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VesselLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point position;

    @Column(name = "speed_kn")
    private Double speedKn;

    @Column(name = "heading_deg")
    private Double headingDeg;

    @Column(name = "rudder_angle")
    private Double rudderAngle;

    private Double throttle;
    private Double roll;
    private Double pitch;
    private Double yaw;
}
