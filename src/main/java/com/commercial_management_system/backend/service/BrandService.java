package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    // cadastra uma nova marca de veiculo
    @Transactional
    public BrandResponse salvar(BrandRequest request) {
        Brand brand = new Brand(
                request.name());

        brandRepository.save(brand);
        return BrandResponse.from(brand);
    }

    // lista todas as marcas cadastradas
    @Transactional(readOnly = true)
    public List<BrandResponse> listarTodos() {
        return brandRepository.findAll().stream()
                .map(BrandResponse::from)
                .toList();
    }

    // busca marca por identificador unico
    @Transactional(readOnly = true)
    public BrandResponse buscarPorId(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("marca nao encontrada"));
        return BrandResponse.from(brand);
    }
}
