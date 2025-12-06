package com.market.cart.entity.model;

public record ModelUpdateDTO(

        Long modelId,

        String name,

        Long makeId,

        Long vehicleTypeId
) {
}
