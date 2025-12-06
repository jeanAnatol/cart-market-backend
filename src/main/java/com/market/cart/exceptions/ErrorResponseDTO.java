package com.market.cart.exceptions;


import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String errorCode,
        String path
) {}
