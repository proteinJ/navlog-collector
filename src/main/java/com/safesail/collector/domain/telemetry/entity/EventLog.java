package com.safesail.collector.domain.telemetry.entity;

import com.safesail.collector.domain.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "event_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(name = "event_time")
    private OffsetDateTime eventTime;

    @Column(name = "severity", length = 20)
    private String severity;

    private String description;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point position;
}
