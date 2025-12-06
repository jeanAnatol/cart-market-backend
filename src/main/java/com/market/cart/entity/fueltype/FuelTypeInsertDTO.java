package com.market.cart.entity.fueltype;

import jakarta.validation.constraints.NotBlank;

public record FuelTypeInsertDTO(
        @NotBlank String name
) {
}
