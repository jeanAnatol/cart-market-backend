package com.market.cart.entity.enums;


import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import lombok.Getter;
import java.util.Arrays;

/**
 * Represents the condition/state of a vehicle in an advertisement.
 *
 * <p>
 * Each enum constant has a readable label used for external input
 * </p>
 */
@Getter
public enum VehicleState {
    NEW("New"),
    USED("Used"),
    ONLY_PARTS("Only parts");

    private final String label;

    VehicleState(String label) {
        this.label = label;
    }

    /**
     * Resolves a {@link VehicleState} from its label (case-insensitive).
     */
    public static VehicleState fromLabel(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new CustomInvalidArgumentException("No vehicle state found with label: " + value, "veehicleState")
                );
    }
}

