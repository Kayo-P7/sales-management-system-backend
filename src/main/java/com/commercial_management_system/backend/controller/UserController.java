package com.commercial_management_system.backend.controller;


import com.commercial_management_system.backend.dto.request.UserRequest;
import com.commercial_management_system.backend.dto.response.UserResponse;
import com.commercial_management_system.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService USER_SERVICE;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse cadastrar(@Valid @RequestBody UserRequest userRequest){
        return UserResponse.from(USER_SERVICE.cadastrar(userRequest));
    }
}
