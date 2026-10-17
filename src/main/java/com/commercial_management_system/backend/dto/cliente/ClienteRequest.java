package com.commercial_management_system.backend.dto.cliente;

public record ClienteRequest(
String name,
String telephone,
String email,
String notes
) {
}
