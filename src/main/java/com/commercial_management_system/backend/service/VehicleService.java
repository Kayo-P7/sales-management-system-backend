package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.VehicleMapper;
import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.model.Vehicle;
import com.commercial_management_system.backend.repository.BrandRepository;
import com.commercial_management_system.backend.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.commercial_management_system.backend.specifications.SpecsVehicle.equalPreco;
import static com.commercial_management_system.backend.specifications.SpecsVehicle.likeColor;
import static com.commercial_management_system.backend.specifications.SpecsVehicle.likeNameBrand;
import static com.commercial_management_system.backend.specifications.SpecsVehicle.nameVehicle;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, BrandRepository brandRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.brandRepository = brandRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Transactional
    public VehicleResponse save(VehicleRegister vehicleRegister) {
        Brand brand = brandRepository.findById(vehicleRegister.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("marca nao encontrada com id: " + vehicleRegister.brandId()));

        Vehicle vehicle = vehicleMapper.toVehicle(vehicleRegister);
        vehicle.setBrand(brand);
        vehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toVehicleResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("veiculo nao encontrado com id: " + id));
        return vehicleMapper.toVehicleResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponse> findVehicle(
            String name,
            String color,
            BigDecimal price,
            String nameBrand,
            Pageable pageable
    ) {
        Specification<Vehicle> specification = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction()
        );

        if (name != null) {
            specification = specification.and(nameVehicle(name));
        }

        if (color != null) {
            specification = specification.and(likeColor(color));
        }

        if (price != null) {
            specification = specification.and(equalPreco(price));
        }

        if (nameBrand != null) {
            specification = specification.and(likeNameBrand(nameBrand));
        }

        return vehicleRepository.findAll(specification, pageable)
                .map(vehicleMapper::toVehicleResponse);
    }

    @Transactional
    public void deleteVehicleById(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("veiculo nao encontrado com id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRegister vehicleRegister) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("veiculo nao encontrado com id: " + id));

        Brand brand = brandRepository.findById(vehicleRegister.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("marca nao encontrada com id: " + vehicleRegister.brandId()));

        vehicleMapper.updateVehicle(vehicleRegister, vehicle);
        vehicle.setBrand(brand);
        vehicleRepository.save(vehicle);

        return vehicleMapper.toVehicleResponse(vehicle);
    }

    @Transactional
    public VehicleResponse updateVehicleBrand(Long id, VehicleRegister vehicleRegister) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("veiculo nao encontrado com id: " + id));

        Brand brand = brandRepository.findById(vehicleRegister.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("marca nao encontrada com id: " + vehicleRegister.brandId()));

        vehicle.setBrand(brand);
        vehicleRepository.save(vehicle);
        return vehicleMapper.toVehicleResponse(vehicle);
    }
}
