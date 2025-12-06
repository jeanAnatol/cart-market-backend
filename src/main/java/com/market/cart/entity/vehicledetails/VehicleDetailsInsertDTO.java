package com.market.cart.entity.vehicledetails;

import jakarta.validation.constraints.*;

public record VehicleDetailsInsertDTO(

        @NotNull(message = "Vehicle type is required")
        Long vehicleTypeId,

        @NotNull(message = "Vehicle make is required")
        Long makeId,

        @NotNull(message = "Vehicle model is required")
        Long modelId,

        @Pattern(regexp = "\\d{4}", message = "Manufacture year must be 4 digits")
        String manufactureYear,

        @NotNull @PositiveOrZero(message = "Mileage cannot be negative")
        Long mileage,

        @NotBlank(message = "Color is required")
        String color,

        @NotBlank(message = "State must be specified: new/used")
        String state,

        @Size(max = 600, message = "Description must not exceed 600 characters")
        String vehicleDescriptionText
) {
}
