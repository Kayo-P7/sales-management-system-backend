package com.commercial_management_system.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HeathController {


    @GetMapping("/test")
    public Map<String, String> health(){
        return Map.of("status","UP");
    }
}
