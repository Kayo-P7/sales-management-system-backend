package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.sale.SaleRequest;
import com.commercial_management_system.backend.dto.sale.SaleResponse;
import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.BusinessException;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.SaleMapper;
import com.commercial_management_system.backend.model.Cliente;
import com.commercial_management_system.backend.model.Sale;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.model.Vehicle;
import com.commercial_management_system.backend.repository.ClienteRepository;
import com.commercial_management_system.backend.repository.SaleRepository;
import com.commercial_management_system.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final VehicleRepository vehicleRepository;
    private final ClienteRepository clienteRepository;
    private final SaleMapper saleMapper;

    // registra uma nova venda vinculada ao vendedor autenticado
    @Transactional
    public SaleResponse save(SaleRequest request) {
        User seller = getUsuarioAutenticado();

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "veiculo nao encontrado com id: " + request.vehicleId()));

        if (saleRepository.existsByVehicleIdAndStatus(vehicle.getId(), SaleStatus.COMPLETED)) {
            throw new BusinessException("este veiculo ja possui uma venda registrada");
        }

        Cliente cliente = clienteRepository.findByIdAndCreatedBy(request.customerId(), seller.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "cliente nao encontrado com id: " + request.customerId()));

        Sale sale = new Sale();
        sale.setVehicle(vehicle);
        sale.setCliente(cliente);
        sale.setSeller(seller);
        sale.setSalePrice(request.salePrice());
        sale.setPaymentMethod(request.paymentMethod());
        sale.setNotes(request.notes());
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);

        return saleMapper.toSaleResponse(saleRepository.save(sale));
    }

    // lista vendas respeitando o escopo de acesso do perfil autenticado
    @Transactional(readOnly = true)
    public Page<SaleResponse> listar(SaleStatus status, Pageable pageable) {
        User usuario = getUsuarioAutenticado();
        Page<Sale> vendas = buscarPorPerfil(usuario, status, pageable);
        return vendas.map(saleMapper::toSaleResponse);
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        Sale sale = buscarVendaAutorizada(id);
        return saleMapper.toSaleResponse(sale);
    }

    // cancela uma venda ativa registrando o motivo informado
    @Transactional
    public void cancelar(Long id, String reason) {
        Sale sale = buscarVendaAutorizada(id);

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("esta venda ja foi cancelada");
        }

        sale.setStatus(SaleStatus.CANCELLED);
        sale.setCancellationReason(reason);
        saleRepository.save(sale);
    }

    private Page<Sale> buscarPorPerfil(User usuario, SaleStatus status, Pageable pageable) {
        if (usuario.getUserType() == UserType.ROLE_ADMIN) {
            return status != null
                    ? saleRepository.findByStatus(status, pageable)
                    : saleRepository.findAll(pageable);
        }

        if (usuario.getUserType() == UserType.ROLE_SUPERVISOR) {
            return status != null
                    ? saleRepository.findBySellerEquipeSupervisorIdAndStatus(usuario.getId(), status, pageable)
                    : saleRepository.findBySellerEquipeSupervisorId(usuario.getId(), pageable);
        }

        if (usuario.getUserType() == UserType.ROLE_VENDEDOR) {
            return status != null
                    ? saleRepository.findBySellerIdAndStatus(usuario.getId(), status, pageable)
                    : saleRepository.findBySellerId(usuario.getId(), pageable);
        }

        throw new AccessDeniedException("usuario sem permissao para consultar vendas");
    }

    private Sale buscarVendaAutorizada(Long id) {
        User usuario = getUsuarioAutenticado();
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("venda nao encontrada com id: " + id));

        if (!podeAcessarVenda(usuario, sale)) {
            throw new AccessDeniedException("acesso negado para esta venda");
        }

        return sale;
    }

    private boolean podeAcessarVenda(User usuario, Sale sale) {
        if (usuario.getUserType() == UserType.ROLE_ADMIN) {
            return true;
        }

        if (usuario.getUserType() == UserType.ROLE_VENDEDOR) {
            return sale.getSeller().getId().equals(usuario.getId());
        }

        if (usuario.getUserType() == UserType.ROLE_SUPERVISOR) {
            return sale.getSeller().getEquipe() != null
                    && sale.getSeller().getEquipe().getSupervisor() != null
                    && sale.getSeller().getEquipe().getSupervisor().getId().equals(usuario.getId());
        }

        return false;
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User usuario)) {
            throw new AccessDeniedException("nao foi possivel identificar o usuario autenticado");
        }

        return usuario;
    }
}
