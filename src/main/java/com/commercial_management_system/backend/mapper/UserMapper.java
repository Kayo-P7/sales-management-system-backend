package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface UserMapper {

    // converte requisicao em entidade usuario ignorando senha para hash posterior
    @Mapping(target = "password", ignore = true)
    User toUser(UserRequest request);

    // converte entidade usuario em dto detalhado de resposta
    UserResponse toUserResponse(User user);

    // converte entidade usuario em dto resumido
    UserSummaryResponse toUserSummaryResponse(User user);
}
