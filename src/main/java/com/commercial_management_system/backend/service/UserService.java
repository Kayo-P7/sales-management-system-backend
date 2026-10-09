package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.exception.UserAlreadyActivedException;
import com.commercial_management_system.backend.exception.UserAlreadyDeactivedException;
import com.commercial_management_system.backend.exception.UserAlreadyExistsException;
import com.commercial_management_system.backend.exception.UserNotFoundException;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // cadastra novo usuario gerando hash seguro para a senha
    @Transactional
    public User salvar(UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException("email ja cadastrado no sistema");
        }
        if (this.userRepository.existsByTelephone(userRequest.telephone())) {
            throw new UserAlreadyExistsException("telefone ja cadastrado no sistema");
        }

        String encodedPassword = this.passwordEncoder.encode(userRequest.password());
        User user = new User(
                userRequest.name(),
                userRequest.email(),
                userRequest.telephone(),
                encodedPassword,
                userRequest.userType(),
                UserStatus.ACTIVE
        );

        return this.userRepository.save(user);
    }

    // busca usuario e converte para dto de resposta
    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        User user = buscarEntidadePorId(id);
        return UserResponse.from(user);
    }

    // busca entidade usuario para uso interno
    @Transactional(readOnly = true)
    public User buscarEntidadePorId(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    // lista usuarios com suporte a paginacao e filtro opcional por nome
    @Transactional(readOnly = true)
    public Page<UserResponse> listar(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return this.userRepository.findByNameContainingIgnoreCase(name.trim(), pageable)
                    .map(UserResponse::from);
        }
        return this.userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    // desativa usuario no sistema sem excluir o registro fisico
    @Transactional
    public void desativar(Long id) {
        User user = buscarEntidadePorId(id);

        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new UserAlreadyDeactivedException();
        }

        user.setUserStatus(UserStatus.INACTIVE);
        this.userRepository.save(user);
    }

    // reativa usuario inativo no sistema
    @Transactional
    public void ativar(Long id) {
        User user = buscarEntidadePorId(id);

        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new UserAlreadyActivedException();
        }

        user.setUserStatus(UserStatus.ACTIVE);
        this.userRepository.save(user);
    }
}
