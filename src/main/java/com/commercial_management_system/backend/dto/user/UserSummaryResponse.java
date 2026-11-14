package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserType;

public record UserSummaryResponse(
        Long id,
        String name,
        String email,
        String telephone,
        UserType userType
) {
}
