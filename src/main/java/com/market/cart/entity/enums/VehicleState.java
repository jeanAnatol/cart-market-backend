package com.market.cart.entity.enums;


import com.market.cart.exceptions.custom.CustomInvalidArgumentException;

import java.util.Arrays;

public enum VehicleState {
    NEW("New"),
    USED("Used"),
    ONLY_PARTS("Only parts");

    private final String label;

    VehicleState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static VehicleState fromLabel(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new CustomInvalidArgumentException("No vehicle state found with label: " + value, "veehicleState")
                );
    }
}

