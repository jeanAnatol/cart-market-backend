package com.market.cart.entity.vehicletype;
import com.market.cart.entity.make.MakeReadOnlyDTO;

import java.util.Set;

public record VehicleTypeReadOnlyDTO(
        Long id,
        String name,
//        Set<String> makes,
        Set<MakeReadOnlyDTO> makes
) {
}
