package com.zimmomo.momo.api.dto.response;

import com.zimmomo.momo.domain.model.enums.AgentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AgentResponse(
    UUID agentId,
    UUID userId,
    String agentCode,
    String businessName,
    String territory,
    BigDecimal commissionRate,
    BigDecimal floatBalance,
    AgentStatus status,
    Instant createdAt
) {}
