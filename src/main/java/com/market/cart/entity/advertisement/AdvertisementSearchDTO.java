package com.market.cart.entity.advertisement;

import jakarta.annotation.Nullable;

public record AdvertisementSearchDTO(

        @Nullable
        Long typeId,
        @Nullable
        Long makeId,
        @Nullable
        Long modelId,
        @Nullable
        String locationName,
        @Nullable
        String postalCode
) {
}
