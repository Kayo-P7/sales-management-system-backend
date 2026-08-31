package com.commercial_management_system.backend.service;


import com.commercial_management_system.backend.dto.request.UserRequest;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.exceptions.UserNotFoundException;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository USER_REPOSITORY;



    @Transactional()
    public User cadastrar(UserRequest userRequest){
        User user = new User(
                userRequest.name(),
                userRequest.email(),
                userRequest.telephone(),
                userRequest.password(),
                userRequest.userType(),
                UserStatus.ACTIVE);

        return USER_REPOSITORY.save(user);
    }

    @Transactional(readOnly = true)
    public User buscarPorId(Long id){
        return USER_REPOSITORY.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<User> listarTodos(){
        return USER_REPOSITORY.findAll();
    }

    @Transactional(readOnly = true)
    public User buscarPorEmail(String email){
        return USER_REPOSITORY.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }


}

