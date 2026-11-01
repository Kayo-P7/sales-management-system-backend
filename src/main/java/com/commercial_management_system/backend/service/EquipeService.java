package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.equipe.AddVendedorRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.BusinessException;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.EquipeMapper;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.EquipeRepository;
import com.commercial_management_system.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public User getUsuarioAuthenticado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof User usuario)) {

            throw new IllegalStateException("nao foi possivel identificar o usuario autenticado");
        }

        return usuario;
    }

    @Transactional
    public EquipeResponse save(EquipeRequest equipeRequest) {

        User usuarioAutenticado = getUsuarioAuthenticado();

        if (equipeRepository.existsBySupervisorId(usuarioAutenticado.getId())) {
            throw new BusinessException("o supervisor ja possui uma equipe");
        }
        if (usuarioAutenticado.getUserType() != UserType.ROLE_SUPERVISOR) {
            throw new BusinessException("apenas supervisores podem criar sua propria equipe");
        }
        Equipe equipe = equipeMapper.toEquipe(equipeRequest);

        equipe.setSupervisor(usuarioAutenticado);

        equipe = equipeRepository.save(equipe);

        for (Long vendedorId : equipeRequest.vendedores()) {
            User vendedor = userRepository.findById(vendedorId)
                    .orElseThrow(() -> new ResourceNotFoundException("vendedor nao encontrado com id: " + vendedorId));

            if (vendedor.getUserType() != UserType.ROLE_VENDEDOR) {
                throw new BusinessException("o usuario informado nao e um vendedor");
            }

            if (vendedor.getEquipe() != null) {
                throw new BusinessException("o vendedor ja pertence a uma equipe");
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
                .orElseThrow(() -> new ResourceNotFoundException("equipe nao encontrada para o vendedor informado"));

        return equipeMapper.toEquipeResponse(equipe);
    }

    @Transactional(readOnly = true)
    public EquipeResponse findAllBySupervisorId() {

        User usuario = getUsuarioAuthenticado();

        Equipe equipe = equipeRepository.findAllBySupervisorId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("equipe do supervisor nao encontrada"));

        return equipeMapper.toEquipeResponse(equipe);
    }

    @Transactional
    public void deleteByVendedor(Long id) {
        User supervisor = getUsuarioAuthenticado();

        if (supervisor.getUserType() != UserType.ROLE_SUPERVISOR) {
            throw new BusinessException("apenas supervisores podem remover vendedores da equipe");
        }

        User vendedor = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vendedor nao encontrado com id: " + id));

        Equipe equipe = vendedor.getEquipe();

        if (equipe == null ||
                equipe.getSupervisor() == null ||
                !equipe.getSupervisor().getId().equals(supervisor.getId())) {
            throw new BusinessException("vendedor nao pertence a sua equipe");
        }

        vendedor.setEquipe(null);
        userRepository.save(vendedor);
    }


    @Transactional
    public EquipeResponse addVendedorAEquipe(AddVendedorRequest request) {

        User supervisor = getUsuarioAuthenticado();

        if (supervisor.getUserType() != UserType.ROLE_SUPERVISOR) {
            throw new BusinessException(
                    "Apenas supervisores podem adicionar vendedores"
            );
        }

        Equipe equipe = equipeRepository.findAllBySupervisorId(supervisor.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipe nao encontrada"
                ));

        User vendedor = userRepository.findById(request.idVendedor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendedor nao encontrado"
                ));

        if (vendedor.getUserType() != UserType.ROLE_VENDEDOR) {
            throw new BusinessException(
                    "O usuario informado nao e um vendedor"
            );
        }

        if (vendedor.getEquipe() != null) {
            throw new BusinessException(
                    "O vendedor ja pertence a uma equipe"
            );
        }

        vendedor.setEquipe(equipe);
        equipe.getVendedores().add(vendedor);

        return equipeMapper.toEquipeResponse(equipe);
    }
}
