package com.market.cart.entity.advertisement;

import com.market.cart.entity.contactinfo.ContactInfoUpdateDTO;
import com.market.cart.entity.enginespec.EngineSpecUpdateDTO;
import com.market.cart.entity.location.LocationUpdateDTO;
import com.market.cart.entity.vehicledetails.VehicleDetailsUpdateDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdvertisementUpdateDTO(

        @NotNull(message = "Advertisement ID is required")
        String adUuid,

        @Nullable
        @Positive(message = "Price must be greater than 0")
        Double price,

        @Nullable
        Boolean deleteOldAttachments,

        @Nullable
        @Valid
        VehicleDetailsUpdateDTO vehicleDetailsUpdateDTO,

        @Nullable
        @Valid  /// EngineSpec
        EngineSpecUpdateDTO engineSpecUpdateDTO,

        @Nullable
        @Valid
        ContactInfoUpdateDTO contactInfoUpdateDTO,

        @Nullable
        @Valid
        LocationUpdateDTO locationUpdateDTO




) {
}
