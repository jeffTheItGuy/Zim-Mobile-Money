package com.zimmomo.momo.api.dto.response;

public record AuthResponse(
    String token,
    String tokenType,
    Long expiresIn
) {}
