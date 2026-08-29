package com.commercial_management_system.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HeathController {


    @GetMapping
    public Map<String, String> health(){
        return Map.of("status","UP");
    }
}
