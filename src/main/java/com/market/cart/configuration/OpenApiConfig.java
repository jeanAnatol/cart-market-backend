package com.market.cart.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>This configuration class customizes the generated OpenAPI documentation
 * and integrates JWT-based authentication into Swagger UI.</p>
 * <h2>JWT integration in Swagger</h2>
 * <p>
 * The {@link io.swagger.v3.oas.annotations.security.SecurityScheme} annotation
 * registers a security scheme named <b>"JWT"</b> that:
 * </p>
 * <ul>
 *   <li>Uses HTTP Bearer authentication</li>
 *   <li>Expects a JWT token</li>
 *   <li>Reads the token from the {@code Authorization} HTTP header</li>
 * </ul>
 * <p>
 * This token will then be automatically sent with all secured API requests.
 * </p>
 *
 * <h2>Global availability</h2>
 * <p>
 * The security scheme is defined globally and can be referenced by controllers
 * or operations using {@code @SecurityRequirement(name = "JWT")}.
 * </p>
 *
 * @see io.swagger.v3.oas.annotations.security.SecurityScheme
 * @see io.swagger.v3.oas.models.OpenAPI
 */
@Configuration
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    /**
     * Creates and configures the {@link OpenAPI} bean used by Springdoc.
     *
     * <p>This bean defines general metadata for the API documentation</p>
     */
    @Bean
    public OpenAPI cartApiDocs() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cart Advertisement API")
                        .version("1.0")
                        .description("""
                                Vehicle marketplace backend API.
                                Includes authentication, administration, user management, and advertisements.
                                """)
                );
    }
}