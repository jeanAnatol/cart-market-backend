package com.market.cart.entity.role;


import jakarta.validation.constraints.NotBlank;



public record RoleInsertDTO(
        @NotBlank(message = "Role name cannot be blank.")
        String name
) {
}
