package com.zimmomo.momo.api.dto.response;

import com.zimmomo.momo.domain.model.enums.UserStatus;
import com.zimmomo.momo.domain.model.enums.UserType;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID userId,
    String phoneNumber,
    String firstName,
    String lastName,
    String nationalId,
    Integer kycLevel,
    UserType userType,
    UserStatus status,
    Instant createdAt
) {}
