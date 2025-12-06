package com.market.cart.exceptions.custom;

public class CustomNotAuthorizedException extends CustomGenericException {

    public CustomNotAuthorizedException(String message, String errorCode) {

      super(message, errorCode);
    }
}
