package com.market.cart.entity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.*;

public record ModelBulkInsertDTO(
        @NotEmpty List<@NotBlank String> modelNames
) {
}
