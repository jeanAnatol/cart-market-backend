package com.market.cart.entity.vehicletype;

import jakarta.validation.constraints.NotBlank;

public record VehicleTypeInsertDTO(
        @NotBlank String name
) {
}
