package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.Brand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class BrandOptimisticLockingTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("deve incrementar versao ao atualizar entidade brand")
    void deveIncrementarVersaoAoAtualizar() {
        // cria e persiste marca inicial
        Brand brand = new Brand();
        brand.setName("Toyota");
        brand.setIsActive(true);

        Brand salva = brandRepository.saveAndFlush(brand);
        Long versaoInicial = salva.getVersion();

        // atualiza nome da marca
        salva.setName("Toyota Motors");
        Brand atualizada = brandRepository.saveAndFlush(salva);

        assertThat(atualizada.getVersion()).isGreaterThan(versaoInicial);
    }

    @Test
    @DisplayName("deve lancar excecao ao tentar atualizar com versao desatualizada")
    void deveLancarExcecaoEmConcorrenciaOtimista() {
        // cria e persiste marca
        Brand brand = new Brand();
        brand.setName("Honda");
        brand.setIsActive(true);

        Brand salva = brandRepository.saveAndFlush(brand);
        entityManager.detach(salva);

        // carrega a mesma entidade em outra instancia
        Brand concorrente = brandRepository.findById(salva.getId()).orElseThrow();
        concorrente.setName("Honda Motors");
        brandRepository.saveAndFlush(concorrente);

        // tenta atualizar a primeira instancia desatualizada
        salva.setName("Honda do Brasil");

        assertThatThrownBy(() -> {
            brandRepository.saveAndFlush(salva);
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
