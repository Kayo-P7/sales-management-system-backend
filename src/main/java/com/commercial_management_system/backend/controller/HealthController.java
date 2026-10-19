package com.commercial_management_system.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "saúde", description = "verificação de integridade e disponibilidade da aplicação")
@RestController
public class HealthController {

    // verifica se a aplicacao esta ativa e respondendo
    @Operation(summary = "verificar integridade da aplicação", description = "retorna o status de integridade do servico para monitoramento")
    @ApiResponse(responseCode = "200", description = "aplicacao operando normalmente",
            content = @Content(examples = @ExampleObject(value = "{\"status\": \"UP\"}")))
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
