package com.zimmomo.momo.api.dto.response;

import com.zimmomo.momo.domain.model.enums.SourceChannel;
import com.zimmomo.momo.domain.model.enums.TransactionStatus;
import com.zimmomo.momo.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
    UUID transactionId,
    String referenceNumber,
    TransactionType transactionType,
    TransactionStatus status,
    BigDecimal amount,
    BigDecimal feeAmount,
    BigDecimal agentCommission,
    String currencyCode,
    UUID senderWalletId,
    UUID receiverWalletId,
    UUID agentId,
    SourceChannel sourceChannel,
    String description,
    Instant createdAt,
    Instant completedAt
) {}
