package com.market.cart.entity.role.capability;

import com.market.cart.entity.role.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CapabilityInsertDTO(
        @NotBlank(message = "Capability name cannot be blank.") String name,
        @NotBlank(message = "Capability description cannot be blank.") String description
) {
}
