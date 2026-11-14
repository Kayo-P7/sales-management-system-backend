package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;

public record UserResponse(
        Long id,
        String name,
        String email,
        String telephone,
        UserType userType,
        UserStatus userStatus
) {
}
