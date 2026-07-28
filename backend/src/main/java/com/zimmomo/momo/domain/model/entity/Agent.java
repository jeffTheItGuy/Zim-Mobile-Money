package com.zimmomo.momo.domain.model.entity;

import com.zimmomo.momo.domain.model.enums.AgentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "agent_id")
    private UUID agentId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "agent_code", nullable = false, unique = true, length = 20)
    private String agentCode;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "territory", length = 100)
    private String territory;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("0.0100");

    @Column(name = "float_balance", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal floatBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AgentStatus status = AgentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
