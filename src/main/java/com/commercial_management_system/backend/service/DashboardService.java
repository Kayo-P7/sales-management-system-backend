package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.dashboard.DashboardResponse;
import com.commercial_management_system.backend.dto.dashboard.SellerPerformanceResponse;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.repository.BrandRepository;
import com.commercial_management_system.backend.repository.ClienteRepository;
import com.commercial_management_system.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final ClienteRepository clienteRepository;
    private final RegistroDiarioService registroDiarioService;

    // consolida indicadores reais do banco de dados para o painel principal
    @Transactional(readOnly = true)
    public DashboardResponse obterMetricas() {
        BigDecimal inventoryValue = vehicleRepository.sumInventoryValue();

        List<RegistroDiarioResponse> registros = registroDiarioService.buscarRegistros();
        List<SellerPerformanceResponse> desempenhoPorVendedor = consolidarDesempenho(registros);

        long totalClosedDeals = desempenhoPorVendedor.stream()
                .mapToLong(SellerPerformanceResponse::closedDeals)
                .sum();

        long totalNegotiations = desempenhoPorVendedor.stream()
                .mapToLong(SellerPerformanceResponse::negotiations)
                .sum();

        return new DashboardResponse(
                vehicleRepository.count(),
                inventoryValue != null ? inventoryValue : BigDecimal.ZERO,
                clienteRepository.count(),
                brandRepository.count(),
                totalClosedDeals,
                totalNegotiations,
                desempenhoPorVendedor
        );
    }

    // agrupa os registros diarios por vendedor respeitando o escopo de acesso do perfil autenticado
    private List<SellerPerformanceResponse> consolidarDesempenho(List<RegistroDiarioResponse> registros) {
        Map<Long, SellerPerformanceResponse> consolidado = new LinkedHashMap<>();

        for (RegistroDiarioResponse registro : registros) {
            if (registro.vendedorId() == null) {
                continue;
            }

            SellerPerformanceResponse atual = consolidado.get(registro.vendedorId());
            long fechamentos = (atual != null ? atual.closedDeals() : 0) + valorOuZero(registro.fechamentos());
            long negociacoes = (atual != null ? atual.negotiations() : 0) + valorOuZero(registro.negociacoes());
            long agendamentos = (atual != null ? atual.appointments() : 0) + valorOuZero(registro.agendamentos());

            consolidado.put(registro.vendedorId(), new SellerPerformanceResponse(
                    registro.vendedorId(),
                    registro.nomeVendedor(),
                    fechamentos,
                    negociacoes,
                    agendamentos
            ));
        }

        return consolidado.values().stream()
                .sorted(Comparator.comparingLong(SellerPerformanceResponse::closedDeals).reversed())
                .toList();
    }

    private long valorOuZero(Integer valor) {
        return valor != null ? valor : 0;
    }
}
