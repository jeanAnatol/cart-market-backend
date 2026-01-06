package com.market.cart.authentication;
import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * will be called from controller when user sends username and password
 */


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;  // Spring

    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) {

        /// Authentication is essentially an authenticated token
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
    );
    User user = (User) authentication.getPrincipal();
    String token = jwtService.createToken(authRequestDTO.getUsername(), user.getRole().getName());

    return new AuthResponseDTO(user.getUsername(), token);
    }
}
