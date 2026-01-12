package com.market.cart;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds application configuration properties related to file uploads.
 * Full value name: {@code app.upload.dir}
 * <p>
 * Using {@link ConfigurationProperties} allows:
 * </p>
 * <ul>
 *  <li>To map configuration value from {@code application.properties} with the prefix {@code app.upload}.</li>
 *  <li>Then with Spring relaxed binding we set {@code String dir}</li>
 * </ul>
 * </p>
 * The mapped value can then be injected into other Spring components
 * to determine where uploaded files should be stored or served from.
 */
@Configuration
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class UploadProperties {
    private String dir;
}
