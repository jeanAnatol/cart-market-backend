package com.market.cart.filters.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Custom {@link AccessDeniedHandler} implementation used by Spring Security
 * when an authenticated user attempts to access a protected resource
 * without sufficient privileges.
 *
 * <p>
 * This handler is triggered after authentication has succeeded, but
 * authorization has failed (HTTP 403 - Forbidden).
 * </p>
 *
 * <p>
 * It returns a custom JSON response instead of the default Spring Security
 * error page and logs the access denial event.
 * </p>
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access-denied scenarios by returning a JSON response with
     * HTTP status 403 (Forbidden).
     *
     * @param request the HTTP request that resulted in an access-denied error
     * @param response the HTTP response to be sent to the client
     * @param accessDeniedException the exception thrown by Spring Security
     *                              when access is denied
     * @throws IOException if writing the response fails
     * @throws ServletException if a servlet-related error occurs
     */
    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("Access denied for user for request: {}. Message: {}", request.getRequestURI(), accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json; charset=UTF-8");

        /// Custom json response
        String json = """
                {
                  "code": "UserNotAuthorized",
                  "description": "User is not allowed to access this route."
                }
                """;

        response.getWriter().write(json);
    }
}
