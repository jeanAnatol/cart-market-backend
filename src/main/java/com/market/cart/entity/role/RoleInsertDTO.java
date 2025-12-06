package com.market.cart.entity.role;

import com.market.cart.entity.user.User;
import com.market.cart.entity.role.capability.Capability;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RoleInsertDTO(
        @NotBlank(message = "Role name cannot be blank.") String name,
        Set<User> users,
        Set<Capability> capabilities
) {
}
