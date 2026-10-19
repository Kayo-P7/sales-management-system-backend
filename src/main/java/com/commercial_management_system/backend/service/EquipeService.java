package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.mapper.EquipeMapper;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.EquipeRepository;
import com.commercial_management_system.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipeService {


    private final EquipeRepository equipeRepository;
    private final UserRepository userRepository;
    private final EquipeMapper equipeMapper;

    public EquipeService(EquipeRepository equipeRepository, UserRepository userRepository, EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.userRepository = userRepository;
        this.equipeMapper = equipeMapper;
    }


    @Transactional
    public EquipeResponse save(EquipeRequest equipeRequest) {

        User usuarioAutenticado = getUsuarioAuthenticado();

        if (equipeRepository.existsBySupervisorId(usuarioAutenticado.getId())) {
            throw new RuntimeException(
                    "O supervisor já possui uma equipe"
            );
        }
        if (usuarioAutenticado.getUserType() != UserType.ROLE_SUPERVISOR) {
            throw new RuntimeException("Apenas supervisores podem criar sua própria equipe");
        }
        Equipe equipe = equipeMapper.toEquipe(equipeRequest);

        equipe.setSupervisor(usuarioAutenticado);

        equipe = equipeRepository.save(equipe);

        for (Long vendedorId : equipeRequest.vendedores()) {
            User vendedor = userRepository.findById(vendedorId)
                    .orElseThrow(() -> new RuntimeException("Vendedor não encontrado"));


            if (vendedor.getUserType() != UserType.ROLE_VENDEDOR) {
                throw new RuntimeException("O usuário informado não é um vendedor");
            }

            if (vendedor.getEquipe() != null) {
                throw new RuntimeException(
                        "O vendedor já pertence a uma equipe"
                );
            }
            vendedor.setEquipe(equipe);
            userRepository.save(vendedor);
        }

        return equipeMapper.toEquipeResponse(equipe);

    }

    @Transactional(readOnly = true)
    public EquipeResponse findBySupervisorIdVendedoresId(Long vendedorId) {

        User usuario = getUsuarioAuthenticado();

        Equipe equipe = equipeRepository
                .findBySupervisor_IdAndVendedores_Id(usuario.getId(), vendedorId)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));

        return equipeMapper.toEquipeResponse(equipe);
    }


    @Transactional(readOnly = true)
    public EquipeResponse findAllBySupervisorId() {

        User usuario = getUsuarioAuthenticado();

        Equipe equipe = equipeRepository.findAllBySupervisorId(usuario.getId()).orElseThrow(() -> new RuntimeException("Equipe do supervisor não encontrada "));


        return equipeMapper.toEquipeResponse(equipe);
    }

    @Transactional
    public void deleteByVendedor(Long id) {
        User supervisor = getUsuarioAuthenticado();

        if (supervisor.getUserType() != UserType.ROLE_SUPERVISOR) {
            throw new RuntimeException("Apenas supervisores podem remover vendedores da equipe");
        }

        User vendedor = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado"));

        Equipe equipe = vendedor.getEquipe();

        if (equipe == null ||
                equipe.getSupervisor() == null ||
                !equipe.getSupervisor().getId().equals(supervisor.getId())) {
            throw new RuntimeException("Vendedor não pertence à sua equipe");
        }

        vendedor.setEquipe(null);
        userRepository.save(vendedor);
    }


    public User getUsuarioAuthenticado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof User usuario)) {

            throw new IllegalStateException("Não foi possivel identificar o usuario autenticado ");
        }

        return usuario;
    }
}
