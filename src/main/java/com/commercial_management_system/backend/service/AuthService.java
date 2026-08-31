package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.request.AuthenticationRequest;
import com.commercial_management_system.backend.dto.request.RegisterRequest;
import com.commercial_management_system.backend.dto.response.AuthenticationResponse;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository USER_REPOSITORY;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final TokenService TOKEN_SERVICE;
    private final AuthenticationManager AUTHENTICATION_MANAGER;

    public AuthenticationResponse login(AuthenticationRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var auth = this.AUTHENTICATION_MANAGER.authenticate(usernamePassword);
        var token = TOKEN_SERVICE.generateToken((User) auth.getPrincipal());
        return new AuthenticationResponse(token);
    }

    public boolean register(RegisterRequest request) {
        if (USER_REPOSITORY.findByEmail(request.getEmail()).isPresent()) {
            return false;
        }
        
        String encryptedPassword = PASSWORD_ENCODER.encode(request.getPassword());
        User newUser = new User(
                request.getName(),
                request.getEmail(),
                request.getTelephone(),
                encryptedPassword,
                request.getUserType()
        );
        USER_REPOSITORY.save(newUser);
        return true;
    }
}
