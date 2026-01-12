package com.market.cart.api;

import com.market.cart.authentication.AuthRequestDTO;
import com.market.cart.authentication.AuthResponseDTO;
import com.market.cart.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for user authentication.
 *
 * <p>
 * Exposes endpoints for validating user credentials and issuing
 * JSON Web Tokens (JWT) used for authenticated requests.
 * </p>
 *
 * <p>
 * This controller acts as the authentication boundary of the application
 * and delegates all authentication logic to {@link AuthenticationService}.
 * </p>
 */

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User login and token generation")
public class AuthRestController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * <p>
     * Validates the provided credentials and, if successful,
     * returns an authentication response containing a JWT token
     * and username.
     * </p>
     *
     * @param requestDTO authentication request containing username and password
     * @return {@link ResponseEntity} containing {@link AuthResponseDTO} and HTTP 200
     *
     * @throws org.springframework.security.core.AuthenticationException
     *         if credentials are invalid
     */
    @Operation(
            summary = "Authenticate user",
            description = "Validates credentials and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authentication(
            @RequestBody AuthRequestDTO requestDTO) {
        log.info("Authentication attempt for user: {}", requestDTO.getUsername());

        AuthResponseDTO responseDTO = authenticationService.authenticate(requestDTO);
        log.info("Authentication successful for user: {}", requestDTO.getUsername());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
