package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.dashboard.DashboardResponse;
import com.commercial_management_system.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<DashboardResponse> obterMetricas() {
        return ResponseEntity.ok(dashboardService.obterMetricas());
    }
}
