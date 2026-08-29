package com.commercial_management_system.backend.service;


import com.commercial_management_system.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository PRODUCT_REPOSITORY;
}
