package com.market.cart.api;

import com.market.cart.authentication.AuthRequestDTO;
import com.market.cart.authentication.AuthResponseDTO;
import com.market.cart.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User login and token generation")
public class AuthRestController {

    private final AuthenticationService authenticationService;

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

        AuthResponseDTO responseDTO = authenticationService.authenticate(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
