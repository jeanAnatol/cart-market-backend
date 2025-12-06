package com.market.cart.entity.vehicledetails;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record VehicleDetailsUpdateDTO(

        @Nullable
        String vehicleType,

        @Nullable
        String make,

        @Nullable
        String model,

        @Nullable
        @Pattern(regexp = "\\d{4}", message = "Manufacture year must be 4 digits")
        String manufactureYear,

        @Nullable
        @PositiveOrZero(message = "Mileage cannot be negative")
        Long mileage,

        @Nullable
        String color,

        @Nullable
        String state,

        @Nullable
        @Size(max = 600, message = "Description must not exceed 600 characters")
        String vehicleDescriptionText
) {
}
