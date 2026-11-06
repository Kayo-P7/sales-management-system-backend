package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.equipe.EquipeResponsePage;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface EquipeMapper {

    // converte requisicao em entidade ignorando associacoes manuais de usuarios
    @Mapping(target = "vendedores", ignore = true)
    Equipe toEquipe(EquipeRequest request);

    // converte entidade equipe em dto de resposta detalhada
    EquipeResponse toEquipeResponse(Equipe equipe);

    // converte usuario em dto resumido
    UserSummaryResponse toUserSummaryResponse(User user);

    // converte equipe em dto resumido de paginacao
    EquipeResponsePage toEquipeResponsePage(Equipe equipe);
}
