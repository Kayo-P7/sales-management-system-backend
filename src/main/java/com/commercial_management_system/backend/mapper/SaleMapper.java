package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.sale.SaleResponse;
import com.commercial_management_system.backend.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface SaleMapper {

    // converte entidade venda em dto de resposta enriquecido com dados das entidades relacionadas
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleName", source = "vehicle.name")
    @Mapping(target = "customerId", source = "cliente.id")
    @Mapping(target = "customerName", source = "cliente.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName", source = "seller.name")
    SaleResponse toSaleResponse(Sale sale);
}
