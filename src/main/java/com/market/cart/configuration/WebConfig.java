package com.market.cart.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for serving static resources.
 *
 * <p>
 * This configuration exposes the application's upload directory as a publicly
 * accessible resource location under the {@code /uploads/**} URL path.
 * </p>
 *
 * <p>
 * Files stored in the directory specified by the {@code app.upload.dir}
 * application property can be accessed via HTTP without passing through
 * a controller.
 * </p>
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
