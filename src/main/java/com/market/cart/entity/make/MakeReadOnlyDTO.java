package com.market.cart.entity.make;

import java.util.Set;

public record MakeReadOnlyDTO(
        Set<String> models,
        Set<String> vehicleTypes
) {
}
