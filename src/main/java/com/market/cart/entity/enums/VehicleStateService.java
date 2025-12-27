package com.market.cart.entity.enums;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VehicleStateService {

    public Set<String> getAllVehicleStates() {
        return Arrays.stream(VehicleState.values())
                .map(VehicleState::getLabel)
                .collect(Collectors.toUnmodifiableSet());
    }

}
