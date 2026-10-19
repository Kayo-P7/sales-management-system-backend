package com.commercial_management_system.backend.dto.vehicle;

import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import java.math.BigDecimal;

public record VehicleRegister(
        String name,
        VehicleColor color,
        VehicleType vehicleType,
        BigDecimal price,
        Long brandId

) {

}
