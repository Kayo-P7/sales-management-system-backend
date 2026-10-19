package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class EquipeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supervisor", ignore = true)
    @Mapping(target = "vendedores", ignore = true)
    public abstract Equipe toEquipe(EquipeRequest request);

    public abstract EquipeResponse toEquipeResponse(Equipe equipe);

    public abstract UserSummaryResponse toUserSummaryResponse(User user);
}
