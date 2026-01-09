package com.market.cart.entity.make;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record MakeInsertDTO(
        @NotBlank String name,
        @NotNull Set<Long> vehicleTypeId
) {
}
