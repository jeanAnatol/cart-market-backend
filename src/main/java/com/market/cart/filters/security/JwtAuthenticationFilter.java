package com.market.cart.filters.security;

import com.market.cart.authentication.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * JWT authentication filter executed once per HTTP request.
 *
 * <p>
 * This filter:
 * </p>
 * <ul>
 *     <li>Extracts the JWT token from the {@code Authorization} header</li>
 *     <li>Validates the token using {@link JwtService}</li>
 *     <li>Loads the associated {@link UserDetails}</li>
 *     <li>Populates the Spring Security {@link SecurityContextHolder}</li>
 * </ul>
 *
 * <p>
 * If the token is missing, invalid, or expired, authentication is not set
 * and the request proceeds to be handled by Spring Security exception handling.
 * </p>
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /// Service responsible for JWT parsing and validation.
    private final JwtService jwtService;

    /// Service used to load user details from persistence.
    private final UserDetailsService userDetailsService;

    /**
     * Performs JWT-based authentication for incoming requests.
     *
     * @param request     incoming HTTP request
     * @param response    outgoing HTTP response
     * @param filterChain security filter chain
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        /// Skip authentication if no Bearer token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).trim();

        try {
            if (jwt != null) {
                username = jwtService.extractSubject(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("JWT authentication successful for request [{} {}]",
                                request.getMethod(),
                                request.getRequestURI());
                    }
                }
            }

        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired for request [{} {}]",
                    request.getMethod(),
                    request.getRequestURI());
            throw new AuthenticationCredentialsNotFoundException("Expired Token", e);

        } catch (JwtException e) {
            log.debug("Invalid JWT token for request [{} {}]",
                    request.getMethod(),
                    request.getRequestURI());
            throw new BadCredentialsException("Invalid Token");
        }
        filterChain.doFilter(request, response);
    }
}
