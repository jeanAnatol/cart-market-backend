package com.market.cart.entity.advertisement;

import com.market.cart.entity.contactinfo.ContactInfoUpdateDTO;
import com.market.cart.entity.enginespec.EngineSpecUpdateDTO;
import com.market.cart.entity.location.LocationUpdateDTO;
import com.market.cart.entity.vehicledetails.VehicleDetailsUpdateDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record AdvertisementUpdateDTO(

        @NotNull(message = "Advertisement ID is required")
        Long adId,      /// *

        @NotNull(message = "User ID is required")
        Long userId,

        @Nullable
        @Positive(message = "Price must be greater than 0")
        Double price,

        @DefaultValue(value = "true")
        String keepOldAttachments,

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
