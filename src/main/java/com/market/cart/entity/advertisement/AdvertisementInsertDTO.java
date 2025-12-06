package com.market.cart.entity.advertisement;

import com.market.cart.entity.contactinfo.ContactInfoInsertDTO;
import com.market.cart.entity.enginespec.EngineSpecInsertDTO;
import com.market.cart.entity.location.LocationInsertDTO;
import com.market.cart.entity.vehicledetails.VehicleDetailsInsertDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdvertisementInsertDTO(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull @Positive(message = "Price must be greater than 0")
        Double price,

        @Valid  /// VehicleDetails
        VehicleDetailsInsertDTO vehicleDetailsInsertDTO,

        @Valid  /// EngineSpec
        EngineSpecInsertDTO engineSpecInsertDTO,

        @Valid  ///ContactInfo
        ContactInfoInsertDTO contactInfoInsertDTO,

        @Valid    /// Location
        LocationInsertDTO locationInsertDTO
) {
}
