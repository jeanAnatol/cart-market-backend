package com.market.cart.entity.make;

import com.market.cart.entity.model.ModelReadOnlyDTO;

import java.util.Set;

public record MakeReadOnlyDTO(
        Long id,
        String name,
        Set<ModelReadOnlyDTO> models,
        Set<String> vehicleTypes
) {
}
