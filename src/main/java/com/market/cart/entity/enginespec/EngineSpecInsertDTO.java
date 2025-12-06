package com.market.cart.entity.enginespec;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EngineSpecInsertDTO(

        @NotNull(message = "Engine displacement is required.")
        @Positive(message = "Engine displacement cannot be negative or zero.")
        Integer displacement,

        @NotNull(message = "Fuel type is required.")
        Long fuelTypeId,

        @NotNull(message = "Gearbox type is required.")
        String gearboxType,

        @NotNull(message = "Horsepower is required.")
        @Positive(message = "Horsepower cannot be negative or 0")
        Integer horsePower
) {
}


///@NotNull and @NotBlank:
///The first is used to validate both string and numeric values
///where the second only for strings
///Trying to validate an Integer with @NotBlank will give an error
///
