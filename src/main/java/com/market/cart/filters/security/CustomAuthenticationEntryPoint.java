package com.market.cart.filters.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Custom {@link AuthenticationEntryPoint} implementation used by Spring Security
 * when an unauthenticated user attempts to access a protected resource.
 *
 * <p>
 * This entry point is triggered before authorization, during the authentication
 * phase, and results in an HTTP 401 (Unauthorized) response.
 * </p>
 *
 * <p>
 * It replaces the default Spring Security authentication error handling
 * with a structured JSON response.
 * </p>
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme by returning a JSON response with
     * HTTP status 401 (Unauthorized).
     *
     * @param request the HTTP request that triggered the authentication failure
     * @param response the HTTP response to be sent to the client
     * @param authException the exception thrown due to missing or invalid authentication
     * @throws IOException if writing the response fails
     * @throws ServletException if a servlet-related error occurs
     * Implemented method from AuthenticationEntryPoint
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.warn("Unauthenticated access attempt: {}", authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        /// Custom error message
        String json = """
                {
                  "code": "UserNotAuthenticated",
                  "description": "User needs to authenticate in order to access this route."
                }
                """;
        response.getWriter().write(json);
    }
}
