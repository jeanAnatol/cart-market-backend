package com.market.cart.entity.location;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record LocationUpdateDTO(

        @Nullable
        String locationName,

        @Nullable
        String postalCode,

        @Nullable
        String longitude,

        @Nullable
        String latitude
) {
}
