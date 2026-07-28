package com.zimmomo.momo.api.dto.response;

import com.zimmomo.momo.domain.model.enums.EntryType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
    UUID entryId,
    UUID transactionId,
    UUID walletId,
    EntryType entryType,
    BigDecimal amount,
    BigDecimal runningBalance,
    String description,
    Instant createdAt
) {}
