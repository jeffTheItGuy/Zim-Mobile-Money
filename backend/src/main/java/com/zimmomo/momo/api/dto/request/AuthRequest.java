package com.zimmomo.momo.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
    @NotBlank String phoneNumber,
    @NotBlank String pin
) {}
