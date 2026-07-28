package com.zimmomo.momo.api.dto.request;

import com.zimmomo.momo.domain.model.enums.UserType;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Size(max = 15) String phoneNumber,
    @NotBlank @Size(min = 4, max = 6) String pin,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @Size(max = 50) String nationalId,
    @NotNull UserType userType
) {}
