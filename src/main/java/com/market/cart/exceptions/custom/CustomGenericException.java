package com.market.cart.exceptions.custom;

import lombok.Getter;

@Getter
public class CustomGenericException extends RuntimeException {
    private final String errorCode;

    public CustomGenericException(String message, String errorCode) {
      super(message);
      this.errorCode = errorCode;
    }
}
