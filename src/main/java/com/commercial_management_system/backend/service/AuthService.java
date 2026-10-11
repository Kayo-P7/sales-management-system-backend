package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.RegisterRequest;
import com.commercial_management_system.backend.exception.UserAlreadyExistsException;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // registra novo usuario com senha criptografada e checagem de unicidade
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("email ja cadastrado no sistema");
        }
        if (userRepository.existsByTelephone(request.telephone())) {
            throw new UserAlreadyExistsException("telefone ja cadastrado no sistema");
        }

        String senhaCriptografada = passwordEncoder.encode(request.password());
        User newUser = new User(
                request.name(),
                request.email(),
                request.telephone(),
                senhaCriptografada,
                request.userType(),
                request.userStatus()
        );
        userRepository.save(newUser);
    }
}
