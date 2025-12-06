package com.market.cart.entity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModelInsertDTO(
        @NotBlank String name,
        @NotNull Long makeId,
        @NotNull Long vehicleTypeId
        ) {
}
