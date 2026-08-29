package com.commercial_management_system.backend.dto;

import com.commercial_management_system.backend.enums.ProductCategory;
import com.commercial_management_system.backend.model.Product;

public record ProductResponse(
        Long id,
        String name,
        String category,
        Double price,
        String description,
        boolean isActive
) {

    public static ProductResponse from(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getDescription(),
                product.isActive()
        );
    }
}
