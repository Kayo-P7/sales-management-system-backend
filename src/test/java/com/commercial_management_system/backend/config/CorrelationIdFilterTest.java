package com.commercial_management_system.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorrelationIdFilterTest {

    private MockMvc mockMvc;
    private final AtomicReference<String> mdcCapturedValue = new AtomicReference<>();

    @RestController
    class FakeTestController {
        @GetMapping("/test-correlation")
        public ResponseEntity<String> test() {
            // captura o valor do mdc durante a execucao da requisicao
            mdcCapturedValue.set(MDC.get(CorrelationIdFilter.MDC_KEY));
            return ResponseEntity.ok("ok");
        }
    }

    @BeforeEach
    void setUp() {
        mdcCapturedValue.set(null);
        CorrelationIdFilter filter = new CorrelationIdFilter();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FakeTestController())
                .addFilters(filter)
                .build();
    }

    // valida que um novo correlation id e gerado no formato uuid quando o cabecalho nao e enviado
    @Test
    @DisplayName("deve gerar novo correlation id em formato uuid quando cabecalho ausente")
    void deveGerarNovoCorrelationIdQuandoCabecalhoAusente() throws Exception {
        mockMvc.perform(get("/test-correlation"))
                .andExpect(status().isOk())
                .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .andDo(result -> {
                    String correlationId = result.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
                    assertNotNull(correlationId);
                    // valida formato uuid
                    UUID.fromString(correlationId);
                    // valida que o mesmo id estava disponivel no mdc durante o processamento
                    assertEquals(correlationId, mdcCapturedValue.get());
                });

        // valida limpeza do mdc apos finalizar a requisicao
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }

    // valida que o correlation id enviado pelo cliente e preservado
    @Test
    @DisplayName("deve preservar correlation id fornecido pelo cliente na requisicao")
    void devePreservarCorrelationIdFornecidoPeloCliente() throws Exception {
        String customId = "req-externa-abc-123";

        mockMvc.perform(get("/test-correlation")
                        .header(CorrelationIdFilter.CORRELATION_ID_HEADER, customId))
                .andExpect(status().isOk())
                .andExpect(header().string(CorrelationIdFilter.CORRELATION_ID_HEADER, customId));

        // valida que o mdc continha o id customizado durante a execucao
        assertEquals(customId, mdcCapturedValue.get());
        // valida limpeza do mdc apos a requisicao
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }
}
