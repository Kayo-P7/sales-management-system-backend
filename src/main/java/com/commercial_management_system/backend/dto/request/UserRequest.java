package com.commercial_management_system.backend.dto.request;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String telephone,
        @NotBlank String password,
        @NotBlank UserType userType
        ) {
}
