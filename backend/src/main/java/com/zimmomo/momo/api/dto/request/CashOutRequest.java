package com.zimmomo.momo.api.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CashOutRequest(
    @NotBlank String idempotencyKey,
    @NotNull @Positive BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currencyCode,
    @NotBlank String customerPhoneNumber,
    @Size(max = 255) String description
) {}
