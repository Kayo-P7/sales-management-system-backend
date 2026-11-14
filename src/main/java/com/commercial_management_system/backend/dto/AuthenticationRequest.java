package com.commercial_management_system.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
