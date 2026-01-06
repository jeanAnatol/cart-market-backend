package com.market.cart.entity.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;

public record UserUpdateDTO(

        @Nullable
        String username,

        @Nullable
        String currentPassword,

        @Nullable
        String newPassword,

        @Nullable
        String email
) {
}
