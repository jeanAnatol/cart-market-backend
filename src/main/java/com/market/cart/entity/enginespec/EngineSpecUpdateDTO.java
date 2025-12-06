package com.market.cart.entity.enginespec;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EngineSpecUpdateDTO(
        @Nullable
        @Positive(message = "Engine displacement cannot be negative or zero.")
        Integer displacement,

        @Nullable
        Long fuelTypeId,

        @Nullable
        String gearboxType,

        @Nullable
        @Positive(message = "Horsepower cannot be negative or 0")
        Integer horsePower
) {
}
