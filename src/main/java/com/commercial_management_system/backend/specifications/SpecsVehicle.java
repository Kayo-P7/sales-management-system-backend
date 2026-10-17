package com.commercial_management_system.backend.specifications;

import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.model.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class SpecsVehicle {

    public static Specification<Vehicle> nameVehicle(String name) {
        return (root, query, cb) -> cb.
                like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }

    public static Specification<Vehicle> likeColor(String color) {
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("color")), "%" + color.toUpperCase() + "%");
    }

    public static Specification<Vehicle> likeNameBrand(String nameBrand) {


        return (root, query, cb) -> {
            Join<Vehicle, Brand> joinBrand = root.join("brand", JoinType.LEFT);
            return cb.like(cb.upper(joinBrand.get("name")), "%" + nameBrand.toUpperCase() + "%");
        };
    }

    public static Specification<Vehicle> equalPreco(BigDecimal price) {
        return (root, query, cb) ->
                cb.equal(root.get("price"), price);
    }
}
