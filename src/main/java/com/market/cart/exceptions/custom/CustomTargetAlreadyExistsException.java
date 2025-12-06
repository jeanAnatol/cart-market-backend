package com.market.cart.exceptions.custom;

public class CustomTargetAlreadyExistsException extends CustomGenericException {

    public CustomTargetAlreadyExistsException(String message, String errorCode) {
      super(message, errorCode);
    }
}
