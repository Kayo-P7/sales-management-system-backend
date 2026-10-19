package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.ClienteMapper;
import com.commercial_management_system.backend.model.Cliente;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public ClienteResponse save(ClienteRequest clienteRequest) {

        Cliente cliente = clienteMapper.toCliente(clienteRequest);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("cliente nao encontrado com id: " + id));
        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional
    public void deleteClienteById(Long id) {
        User usuario = getUsuarioAutenticado();

        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("cliente nao encontrado com id: " + id));
        clienteRepository.delete(cliente);
    }

    @Transactional
    public ClienteResponse updateCliente(Long id, ClienteRequest clienteRequest) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository
                .findByIdAndCreatedBy(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("cliente nao encontrado com id: " + id));

        clienteMapper.updateCliente(clienteRequest, cliente);

        clienteRepository.save(cliente);

        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional
    public ClienteResponse updateClienteParcial(Long id, ClienteRequest clienteRequest) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("cliente nao encontrado com id: " + id));

        clienteMapper.parcialPatchUpdate(clienteRequest, cliente);

        clienteRepository.save(cliente);
        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> findByName(String name, Pageable pageable) {
        User usuario = getUsuarioAutenticado();
        Page<Cliente> clientes = clienteRepository.findByNameContainingIgnoreCaseAndCreatedBy(name, usuario.getId(), pageable);

        return clientes.map(clienteMapper::toClientResponse);
    }

    @Transactional(readOnly = true)
    public ClienteResponse findByTelephone(String telephone) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository
                .findByCreatedByAndTelephoneContaining(usuario.getId(), telephone)
                .orElseThrow(() -> new ResourceNotFoundException("cliente nao encontrado com telefone: " + telephone));

        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> listarClientes(Pageable pageable) {
        User usuario = getUsuarioAutenticado();

        return clienteRepository
                .findAllByCreatedBy(usuario.getId(), pageable)
                .map(clienteMapper::toClientResponse);
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User usuario)) {
            throw new IllegalStateException("nao foi possivel identificar o usuario autenticado");
        }

        return usuario;
    }

}
