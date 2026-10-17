package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
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
        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toClientResponse(cliente);
    }


    @Transactional
    public void deleteClienteById(Long id) {
        User usuario = getUsuarioAutenticado();

        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId()).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        clienteRepository.delete(cliente);
    }

    @Transactional
    public ClienteResponse updateCliente(Long id, ClienteRequest clienteRequest) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository
                .findByIdAndCreatedBy(id, usuario.getId()).orElseThrow(() -> new RuntimeException("Id not found"));

        clienteMapper.updateCliente(clienteRequest, cliente);

        clienteRepository.save(cliente);

        return clienteMapper.toClientResponse(cliente);
    }

    @Transactional
    public ClienteResponse updateClienteParcial(Long id, ClienteRequest clienteRequest) {

        User usuario = getUsuarioAutenticado();// mostra quem é o usuario que se autenticou
        //só pode ser atualizado pelo responsavel que criou esse cliente
        Cliente cliente = clienteRepository.findByIdAndCreatedBy(id, usuario.getId()).orElseThrow(() -> new RuntimeException("Id not found"));

        clienteMapper.parcialPatchUpdate(clienteRequest, cliente);

        clienteRepository.save(cliente);
        return clienteMapper.toClientResponse(cliente);
    }


    public Page<ClienteResponse> findByName(String name, Pageable pageable) {

        User usuario = getUsuarioAutenticado();
        Page<Cliente> clientes = clienteRepository.findByNameAndCreatedByContainingIgnoreCase(usuario.getId(), name, pageable);

        return clientes.map(clienteMapper::toClientResponse);
    }


    public ClienteResponse findByTelephone(String telephone) {
        User usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository
                .findByCreatedByAndTelephoneContaining(usuario.getId(), telephone)
                .orElseThrow(() -> new RuntimeException("Telephone not found"));

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
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof User usuario)) {
            throw new
                    IllegalStateException
                    ("Não foi possivel identificar o usuario autenticado ");
        }

        return usuario;
    }

}
