package com.safesail.collector.domain.report.entity;

import com.safesail.collector.domain.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "evaluation_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", unique = true)
    private Session session;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "collision_count")
    private Integer collisionCount;

    @Column(name = "route_deviation_count")
    private Integer routeDeviationCount;

    @Column(name = "speeding_count")
    private Integer speedingCount;

    @Column(name = "grounding_risk_count")
    private Integer groundingRiskCount;

    private Boolean passed;

    private String feedback;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
