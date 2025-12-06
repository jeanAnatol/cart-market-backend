package com.market.cart.entity.user;

import com.market.cart.entity.advertisement.Advertisement;
import com.market.cart.entity.role.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.lang.NonNull;

import java.util.Set;

public record UserInsertDTO(

//        @NotEmpty(message = "Username is required.")
        String username,

//        @NotEmpty(message = "Password is required.")
//        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[\\d])(?=.*?[!@#$%^&*()-+=]).{8,}$",
//                message = "Password needs to be 8 characters long and contain one of the following characters:"+
//                                "0-9, A-Z, a-z, !@#$%^&*()-+=")
        String password,

//        @NotEmpty(message = "Email is required.")
//        @Pattern(regexp = "^(.+)@(.+)$", message = "Email is invalid.")
        String email,

        Long roleId
) {
}
