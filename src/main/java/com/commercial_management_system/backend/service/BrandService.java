package com.commercial_management_system.backend.service;


import com.commercial_management_system.backend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository BRAND_REPOSITORY;
}
