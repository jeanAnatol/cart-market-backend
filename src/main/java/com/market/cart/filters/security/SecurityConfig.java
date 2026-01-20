package com.market.cart.filters.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Central Spring Security configuration for the application.
 *
 * <p>
 * This configuration:
 * </p>
 * <ul>
 *     <li>Defines JWT-based stateless authentication</li>
 *     <li>Configures endpoint authorization rules</li>
 *     <li>Registers custom authentication and access-denied handlers</li>
 *     <li>Enables CORS for frontend integration</li>
 * </ul>
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Configures the main Spring Security filter chain.
     *
     * @param http                    HTTP security configuration
     * @param authenticationProvider  authentication provider implementation
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {

        log.info("Initializing Spring Security filter chain");

        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/authentication/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v2/api-docs"
                        ).permitAll()
                        .requestMatchers(
                                "/api/advertisements/all",
                                "/api/advertisements/paginated"
                        ).permitAll()
                        .requestMatchers("/api/users/new").permitAll()
                        .requestMatchers("/api/reference/**").permitAll()
                        .requestMatchers(
                                "/api/users/update/**",
                                 "/api/users/delete/**"
                        ).authenticated()
                        .requestMatchers("/api/users/find/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")
                        .requestMatchers("/api/advertisements/attachments/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) /// LOG-IN FORM (αντίστοιχο)
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(myCustomAuthenticationEntryPoint())
                        .accessDeniedHandler(myCustomAccessDeniedHandler()));
        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS).
     *
     * <p>
     * Allows requests from frontend development servers
     * and supports Authorization headers.
     * </p>
     *
     * @return CORS configuration source
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        log.debug("Configuring CORS settings");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("*"));  // "POST", "PUT" etc
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);        // to allow Authorization header, otherwise browser blocks the Authorization header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Defines the authentication provider using DAO-based authentication.
     *
     * @param userDetailsService user details service
     * @param passwordEncoder   password encoder
     * @return configured {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        log.debug("Registering DAO authentication provider");

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Exposes the authentication manager bean.
     *
     * @param config authentication configuration
     * @return authentication manager
     * @throws Exception if creation fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Password encoder used for hashing user passwords.
     *
     * <p>
     * BCrypt with strength 12 provides strong resistance
     * against brute-force attacks.
     * </p>
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Initializing BCryptPasswordEncoder (strength=12)");
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Custom handler for HTTP 403 Forbidden responses.
     *
     * <p>
     * Triggered when an authenticated user lacks sufficient privileges.
     * </p>
     *
     * @return access denied handler
     */
    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
     * Custom handler for HTTP 401 Unauthorized responses.
     *
     * <p>
     * Triggered when unauthenticated access is attempted
     * on a secured endpoint.
     * </p>
     *
     * @return authentication entry point
     */
    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}