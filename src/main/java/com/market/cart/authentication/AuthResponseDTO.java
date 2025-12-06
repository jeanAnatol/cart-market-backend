package com.market.cart.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *This is the response of the server to the client(user)
 * The username is included in order to compose a greeting to the user after successful login
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private String username;
    private String token;
}

