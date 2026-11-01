package com.commercial_management_system.backend.dto.equipe;

import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.model.Equipe;

import java.util.List;

public record EquipeResponsePage(Long id, String nome,
       UserSummaryResponse supervisor,
                                 List<UserSummaryResponse> vendedores) {

}
