package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.mapper.RegistroDiarioMapper;
import com.commercial_management_system.backend.model.RegistroDiario;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.RegistroDiarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistroDiarioService {

    private final RegistroDiarioRepository registroDiarioRepository;
    private final RegistroDiarioMapper registroDiarioMapper;

    private User getUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {

            throw new AccessDeniedException("Usuário não autenticado.");
        }

        return user;
    }

    @Transactional
    public RegistroDiarioResponse criarRegistro(RegistroDiarioRequest request) {

        User vendedor = getUsuarioAutenticado();

        if (vendedor.getUserType() != UserType.ROLE_VENDEDOR) {
            throw new AccessDeniedException(
                    "Somente vendedores podem criar registros diários."
            );
        }

        RegistroDiario registro =
                registroDiarioMapper.toRegistroDiario(request);

        registro.setVendedor(vendedor);
        registro.setData(LocalDate.now());

        RegistroDiario registroSalvo =
                registroDiarioRepository.save(registro);

        return registroDiarioMapper.toRegistroDiarioResponse(registroSalvo);
    }

    @Transactional(readOnly = true)
    public List<RegistroDiarioResponse> buscarRegistros() {

        User usuario = getUsuarioAutenticado();

        List<RegistroDiario> registros;

        if (usuario.getUserType() == UserType.ROLE_ADMIN) {

            registros = registroDiarioRepository.findAll();

        } else if (usuario.getUserType() == UserType.ROLE_SUPERVISOR) {

            registros = registroDiarioRepository
                    .findAllBySupervisorId(usuario.getId());

        } else if (usuario.getUserType() == UserType.ROLE_VENDEDOR) {

            registros = registroDiarioRepository
                    .findAllByVendedorId(usuario.getId());

        } else {

            throw new AccessDeniedException(
                    "Você não possui permissão para visualizar esses registros."
            );
        }

        return registros.stream()
                .map(registroDiarioMapper::toRegistroDiarioResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroDiarioResponse buscarMeuRegistroPorData(LocalDate data) {

        User vendedor = getUsuarioAutenticado();

        if (vendedor.getUserType() != UserType.ROLE_VENDEDOR) {
            throw new AccessDeniedException(
                    "Somente vendedores podem acessar este recurso."
            );
        }

        RegistroDiario registro = registroDiarioRepository
                .findByVendedorIdAndData(vendedor.getId(), data)
                .orElseThrow(() ->
                        new RuntimeException("Registro diário não encontrado."));

        return registroDiarioMapper.toRegistroDiarioResponse(registro);
    }
<<<<<<< HEAD
=======


    public void deleteRegistroDiario(Long idTarefa){
        User usuario = getUsuarioAutenticado();


        if(usuario.getUserType() != UserType.ROLE_VENDEDOR){
            throw new AccessDeniedException("Voce nao pode acessar esse recurso");
        }

        RegistroDiario registroDiario = registroDiarioRepository
                .findByVendedor_IdAndId(usuario.getId(), idTarefa)
                .orElseThrow(() -> new RuntimeException("voce nao e dono dessa tarefa"));


        registroDiarioRepository.deleteById(registroDiario.getId());

    }

>>>>>>> b22d654 (feat: implementar exclusão de registro diário)
}