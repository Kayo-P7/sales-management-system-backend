package com.commercial_management_system.backend.dto.response;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        String telephone,
        UserType userType,
        UserStatus userStatus

) {

    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTelephone(),
                user.getUserType(),
                user.getUserStatus());
    }
}
