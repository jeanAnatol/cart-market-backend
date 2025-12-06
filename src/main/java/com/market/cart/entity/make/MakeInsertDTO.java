package com.market.cart.entity.make;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MakeInsertDTO(
        @NotBlank String name,
        @NotNull Long vehicleTypeId
) {
}
