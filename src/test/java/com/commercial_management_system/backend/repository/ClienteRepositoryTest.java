package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("deve buscar cliente por id e criador com sucesso")
    void deveBuscarClientePorIdECriador() {
        // prepara entidade com dados completos para persistencia
        Cliente cliente = new Cliente();
        cliente.setName("Cliente Teste");
        cliente.setTelephone("11999998888");
        cliente.setEmail("teste@cliente.com");
        cliente.setCreatedBy(1L);
        cliente.setCreatedAt(LocalDateTime.now());
        cliente.setUpdatedAt(LocalDateTime.now());

        entityManager.persist(cliente);
        entityManager.flush();

        Optional<Cliente> resultado = clienteRepository.findByIdAndCreatedBy(cliente.getId(), 1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getName()).isEqualTo("Cliente Teste");
    }

    @Test
    @DisplayName("nao deve encontrar cliente quando criador for diferente")
    void naoDeveEncontrarClienteQuandoCriadorForDiferente() {
        // prepara entidade persistida para usuario 1
        Cliente cliente = new Cliente();
        cliente.setName("Cliente Outro");
        cliente.setTelephone("11888887777");
        cliente.setCreatedBy(1L);
        cliente.setCreatedAt(LocalDateTime.now());
        cliente.setUpdatedAt(LocalDateTime.now());

        entityManager.persist(cliente);
        entityManager.flush();

        Optional<Cliente> resultado = clienteRepository.findByIdAndCreatedBy(cliente.getId(), 2L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("deve buscar clientes paginados pelo id do criador")
    void deveBuscarClientesPaginadosPorCriador() {
        // prepara dois clientes criados pelo mesmo usuario
        Cliente c1 = new Cliente();
        c1.setName("Alice");
        c1.setTelephone("11111111");
        c1.setCreatedBy(10L);
        c1.setCreatedAt(LocalDateTime.now());
        c1.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(c1);

        Cliente c2 = new Cliente();
        c2.setName("Bob");
        c2.setTelephone("22222222");
        c2.setCreatedBy(10L);
        c2.setCreatedAt(LocalDateTime.now());
        c2.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(c2);
        entityManager.flush();

        Page<Cliente> pagina = clienteRepository.findAllByCreatedBy(10L, PageRequest.of(0, 10));

        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve filtrar clientes por nome ignorando maiusculas e minusculas")
    void deveFiltrarClientesPorNomeCaseInsensitive() {
        // prepara cliente com nome especifico
        Cliente c = new Cliente();
        c.setName("Carlos Eduardo");
        c.setTelephone("33333333");
        c.setCreatedBy(5L);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(c);
        entityManager.flush();

        Page<Cliente> resultado = clienteRepository.findByNameContainingIgnoreCaseAndCreatedBy("carlos", 5L, PageRequest.of(0, 10));

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getName()).isEqualTo("Carlos Eduardo");
    }
}
