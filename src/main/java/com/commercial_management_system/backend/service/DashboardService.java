package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.dashboard.DashboardResponse;
import com.commercial_management_system.backend.dto.dashboard.SalesEvolutionResponse;
import com.commercial_management_system.backend.dto.dashboard.SellerPerformanceResponse;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.projection.SalesEvolutionProjection;
import com.commercial_management_system.backend.repository.BrandRepository;
import com.commercial_management_system.backend.repository.ClienteRepository;
import com.commercial_management_system.backend.repository.SaleRepository;
import com.commercial_management_system.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final SaleRepository saleRepository;
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

        YearMonth mesAtual = YearMonth.now();

        LocalDateTime inicio = mesAtual
                .minusMonths(5)
                .atDay(1)
                .atStartOfDay();

        LocalDateTime fim = mesAtual
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        List<SalesEvolutionProjection> resultados =
                buscarEvolucaoPorPerfil(inicio, fim);

        List<SalesEvolutionResponse> salesEvolution =
                consolidarEvolucao(resultados);

        return new DashboardResponse(
                vehicleRepository.count(),
                inventoryValue != null ? inventoryValue : BigDecimal.ZERO,
                clienteRepository.count(),
                brandRepository.count(),
                totalClosedDeals,
                totalNegotiations,
                desempenhoPorVendedor,
                salesEvolution
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

    private List<SalesEvolutionProjection> buscarEvolucaoPorPerfil(
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User usuario)) {
            throw new IllegalStateException("Usuário autenticado não encontrado.");
        }

        UserType perfil = usuario.getUserType();

        if (perfil == UserType.ROLE_ADMIN) {
            return saleRepository.findEvolutionForAdmin(
                    SaleStatus.COMPLETED, inicio, fim
            );
        }

        if (perfil == UserType.ROLE_SUPERVISOR) {
            return saleRepository.findEvolutionForSupervisor(
                    SaleStatus.COMPLETED, inicio, fim, usuario.getId()
            );
        }

        if (perfil == UserType.ROLE_VENDEDOR) {
            return saleRepository.findEvolutionForSeller(
                    SaleStatus.COMPLETED, inicio, fim, usuario.getId()
            );
        }

        throw new IllegalStateException("Perfil de usuário não reconhecido.");
    }



    private List<SalesEvolutionResponse> consolidarEvolucao(
            List<SalesEvolutionProjection> resultados
    ) {
        Map<String, SalesEvolutionProjection> resultadosPorMes = new HashMap<>();

        for (SalesEvolutionProjection resultado : resultados) {
            resultadosPorMes.put(resultado.getPeriod(), resultado);
        }

        YearMonth mesAtual = YearMonth.now();
        YearMonth primeiroMes = mesAtual.minusMonths(5);

        List<SalesEvolutionResponse> evolucao = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < 6; i++) {
            YearMonth mes = primeiroMes.plusMonths(i);
            String periodo = mes.format(formatter);

            SalesEvolutionProjection resultado = resultadosPorMes.get(periodo);

            evolucao.add(new SalesEvolutionResponse(
                    periodo,
                    resultado != null ? resultado.getTotalRevenue() : BigDecimal.ZERO,
                    resultado != null ? resultado.getTotalSales() : 0L
            ));
        }


        return evolucao;
    }
}
