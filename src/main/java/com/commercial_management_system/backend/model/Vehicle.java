package com.commercial_management_system.backend.model;


import com.commercial_management_system.backend.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Data
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "color", nullable = false)
    private Color color;
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicleType", nullable = false)
    private VehicleType vehicleType;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;


    public Vehicle(String name, Color color, VehicleType vehicleType, BigDecimal price, Brand brand) {
        this.name = name;
        this.color = color;
        this.vehicleType = vehicleType;
        this.price = price;
        this.brand = brand;
    }
}
