package com.market.cart.configuration;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "JWT")
public @interface SecuredApi {
}
