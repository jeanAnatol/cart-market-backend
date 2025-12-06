package com.market.cart.entity.location;

import com.market.cart.entity.advertisement.Advertisement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Geometry;

public record LocationInsertDTO(

        @NotBlank(message = "Location name is required.")
        String locationName,

        @NotBlank(message = "Postal code is required.")
        String postalCode,

        @NotBlank(message = "Longitude is required.")
        String longitude,

        @NotBlank(message = "Latitude is required.")
        String latitude
) {
}
