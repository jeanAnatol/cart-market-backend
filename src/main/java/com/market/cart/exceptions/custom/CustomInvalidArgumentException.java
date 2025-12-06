package com.market.cart.exceptions.custom;

public class CustomInvalidArgumentException extends CustomGenericException {



    public CustomInvalidArgumentException(String message, String errorCode) {
      super(message, errorCode);
    }
}
