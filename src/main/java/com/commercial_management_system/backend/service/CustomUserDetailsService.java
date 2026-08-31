package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.exceptions.UserNotFoundException;
import com.commercial_management_system.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository USER_REPOSITORY;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return USER_REPOSITORY.findByEmail(username).orElseThrow(UserNotFoundException::new);
    }
}
