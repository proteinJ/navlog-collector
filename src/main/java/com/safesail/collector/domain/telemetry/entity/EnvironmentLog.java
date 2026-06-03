package com.safesail.collector.domain.telemetry.entity;

import com.safesail.collector.domain.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "environment_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_direction")
    private Double windDirection;

    @Column(name = "wave_height")
    private Double waveHeight;

    @Column(name = "current_speed")
    private Double currentSpeed;

    @Column(name = "current_direction")
    private Double currentDirection;

    @Column(name = "tide_level")
    private Double tideLevel;

    private Double visibility;
}
