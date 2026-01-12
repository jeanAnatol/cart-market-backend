package com.market.cart.authentication;
import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service responsible for authenticating users and issuing JWT tokens.
 *
 * <p>
 * This service is invoked by the authentication controller when a user
 * submits valid login credentials. Authentication is delegated to Spring
 * Security's {@link AuthenticationManager}.
 * </p>
 *
 * <p>
 * On successful authentication, a JWT token is generated using {@link JwtService}
 * and returned to the client.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final JwtService jwtService;

    /**
     * Spring Security authentication manager used to validate credentials.
     */
    private final AuthenticationManager authenticationManager;  // Spring

    /**
     * Authenticates a user using username and password and returns a JWT token.
     *
     * <p>
     * The authentication process:
     * </p>
     * <ol>
     *     <li>Delegates credential validation to Spring Security</li>
     *     <li>Retrieves the authenticated {@link User} principal</li>
     *     <li>Generates a JWT token containing the user's role</li>
     * </ol>
     *
     * @param authRequestDTO the authentication request containing username and password
     * @return {@link AuthResponseDTO} containing the authenticated username and JWT token
     */
    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) {

        log.info("Authentication attempt for user :{}", authRequestDTO.getUsername());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = jwtService.createToken(authRequestDTO.getUsername(), user.getRole().getName());
        log.info("User: [{}] authenticated successfully", user.getUsername());

        return new AuthResponseDTO(user.getUsername(), token);
    }
}
