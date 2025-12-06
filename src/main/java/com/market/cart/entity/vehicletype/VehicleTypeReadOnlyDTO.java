package com.market.cart.entity.vehicletype;
import java.util.Set;

public record VehicleTypeReadOnlyDTO(
        String name,
        Set<String> makes
) {
}
