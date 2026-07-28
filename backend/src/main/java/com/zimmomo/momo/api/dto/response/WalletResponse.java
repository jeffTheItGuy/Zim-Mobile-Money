package com.zimmomo.momo.api.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletResponse(
    UUID walletId,
    UUID userId,
    String currencyCode,
    BigDecimal balance,
    BigDecimal dailyLimit,
    BigDecimal monthlyLimit,
    Boolean isActive,
    Instant createdAt
) {}
