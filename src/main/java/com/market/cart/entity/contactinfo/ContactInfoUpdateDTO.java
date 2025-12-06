package com.market.cart.entity.contactinfo;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactInfoUpdateDTO(

        @Nullable
        String sellerName,

        @Nullable
        @Pattern(regexp = "^(.+)@(.+)$", message = "Email is invalid. Email should be in the form of \"example@email.com\"")
        String email,

        @Nullable
//        @Pattern(regexp = "^(?:\\+30|0030)?69\\d{8}$", message = "Invalid phone number. Must start with 69 and needs to be 10 digits long.")
        String telephoneNumber1,

        @Nullable
//        @Pattern(regexp = "^(?:\\+30|0030)?69\\d{8}$", message = "Invalid phone number. Must start with 69 and needs to be 10 digits long.")
        String telephoneNumber2
) {
}
