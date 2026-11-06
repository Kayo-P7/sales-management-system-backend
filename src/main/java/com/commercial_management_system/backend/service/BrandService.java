package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.BrandMapper;
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
    private final BrandMapper brandMapper;

    // cadastra uma nova marca de veiculo
    @Transactional
    public BrandResponse salvar(BrandRequest request) {
        Brand brand = brandMapper.toBrand(request);
        brandRepository.save(brand);
        return brandMapper.toBrandResponse(brand);
    }

    // lista todas as marcas cadastradas
    @Transactional(readOnly = true)
    public List<BrandResponse> listarTodos() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toBrandResponse)
                .toList();
    }

    // busca marca por identificador unico
    @Transactional(readOnly = true)
    public BrandResponse buscarPorId(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("marca nao encontrada"));
        return brandMapper.toBrandResponse(brand);
    }
}
