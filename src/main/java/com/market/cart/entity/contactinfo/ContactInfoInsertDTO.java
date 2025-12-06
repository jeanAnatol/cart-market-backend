package com.market.cart.entity.contactinfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactInfoInsertDTO(

        @NotBlank(message = "Seller name is required.")
        String sellerName,

        @NotBlank(message = "Email is required.")
        @Pattern(regexp = "^(.+)@(.+)$", message = "Email is invalid. Email should be in the form of \"example@email.com\"")
        String email,

        @NotBlank(message = "Telephone number is required.")
//        @Pattern(regexp = "^(?:\\+30|0030)?69\\d{8}$", message = "Invalid phone number. Must start with 69 and needs to be 10 digits long.")
        String telephoneNumber1,

//        @Pattern(regexp = "^(?:\\+30|0030)?69\\d{8}$", message = "Invalid phone number. Must start with 69 and needs to be 10 digits long.")
        String telephoneNumber2
) {
}
