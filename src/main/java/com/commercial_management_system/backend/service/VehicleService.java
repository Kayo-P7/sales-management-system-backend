package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.mapper.VehicleMapper;
import com.commercial_management_system.backend.model.Vehicle;
import com.commercial_management_system.backend.specifications.SpecsVehicle;
import com.commercial_management_system.backend.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.commercial_management_system.backend.specifications.SpecsVehicle.*;

@Service
public class VehicleService {


    private VehicleRepository vehicleRepository;
    private VehicleMapper vehicleMapper;
    private SpecsVehicle specs;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper, SpecsVehicle specs) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.specs = specs;
    }

    @Transactional
    public VehicleResponse save(VehicleRegister vehicleRegister) {

        Vehicle vehicle = vehicleMapper.toVehicle(vehicleRegister);
        vehicleRepository.save(vehicle);
        return new VehicleResponse(vehicle.getName(),
                vehicle.getColor(),
                vehicle.getVehicleType(),
                vehicle.getPrice(),
                vehicle.getBrand().getId());
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
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

        Specification<Vehicle> specification = Specification.where
                ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());


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
                .map(vehicleMapper::toVehicleResponseSpecs);
    }

    public void deleteVehicleById(Long id) {

        vehicleRepository.deleteById(id);
    }

    public void deleteVehicleByName(String name) {
        vehicleRepository.deleteByName(name);
    }

    public VehicleResponse updateVehicle(Long id, VehicleRegister vehicleRegister) {
        Vehicle vehicle = vehicleRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        vehicleMapper.updateVehicle(vehicleRegister, vehicle);
        vehicleRepository.save(vehicle);

        return vehicleMapper.toVehicleResponse(vehicle);
    }

    public VehicleResponse updateVehicleBrand(Long id, VehicleRegister vehicleRegister){

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        vehicleMapper.updateVehicleBrand(vehicleRegister, vehicle);

        vehicleRepository.save(vehicle);
        return vehicleMapper.toVehicleResponse(vehicle);
    }
}
