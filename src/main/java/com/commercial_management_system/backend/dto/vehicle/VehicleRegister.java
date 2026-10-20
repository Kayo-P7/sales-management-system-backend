package com.commercial_management_system.backend.dto.vehicle;

import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "dados para cadastro e atualizacao de veiculo")
public record VehicleRegister(
        @Schema(description = "nome ou modelo do veiculo", example = "Corolla")
        @NotBlank(message = "o nome do veiculo e obrigatorio")
        @Size(max = 255, message = "o nome deve ter no maximo 255 caracteres")
        String name,

        @Schema(description = "cor do veiculo", example = "PRETO")
        @NotNull(message = "a cor e obrigatoria")
        VehicleColor color,

        @Schema(description = "tipo do veiculo", example = "CAR")
        @NotNull(message = "o tipo de veiculo e obrigatorio")
        VehicleType vehicleType,

        @Schema(description = "preco de venda", example = "120000.00")
        @NotNull(message = "o preco e obrigatorio")
        @Positive(message = "o preco deve ser maior que zero")
        BigDecimal price,

        @Schema(description = "identificador da marca associada", example = "1")
        @NotNull(message = "o identificador da marca e obrigatorio")
        @Positive(message = "o identificador da marca deve ser valido")
        Long brandId
) {
}
