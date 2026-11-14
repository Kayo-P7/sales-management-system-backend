package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String telephone,
        @NotBlank String password,
        @NotNull UserType userType
) {
}
