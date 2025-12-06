package com.market.cart.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds JWT Authorization: Bearer <token> to Swagger UI.
 * Makes it globally available.
 * Security requirement will be added to all admin endpoints.
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