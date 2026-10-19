package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.repository.BrandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    // valida cadastro de marca com sucesso
    @Test
    @DisplayName("deve salvar marca com sucesso")
    void deveSalvarMarcaComSucesso() {
        BrandRequest request = new BrandRequest("Toyota");
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Toyota");

        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse response = brandService.salvar(request);

        assertNotNull(response);
        assertEquals("Toyota", response.name());
        verify(brandRepository).save(any(Brand.class));
    }

    // valida listagem de todas as marcas cadastradas
    @Test
    @DisplayName("deve listar todas as marcas cadastradas")
    void deveListarTodasAsMarcas() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Honda");

        when(brandRepository.findAll()).thenReturn(List.of(brand));

        List<BrandResponse> resultado = brandService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Honda", resultado.get(0).name());
        verify(brandRepository).findAll();
    }

    // valida busca de marca existente por identificador unico
    @Test
    @DisplayName("deve buscar marca por id com sucesso")
    void deveBuscarMarcaPorIdComSucesso() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Ford");

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        BrandResponse response = brandService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Ford", response.name());
        verify(brandRepository).findById(1L);
    }

    // valida lancamento de excecao quando marca nao for encontrada
    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando marca nao existir")
    void deveLancarExcecaoQuandoMarcaNaoExistir() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brandService.buscarPorId(99L));
        verify(brandRepository).findById(99L);
    }
}
