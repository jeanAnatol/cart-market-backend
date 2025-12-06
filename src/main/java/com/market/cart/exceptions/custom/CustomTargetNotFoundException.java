package com.market.cart.exceptions.custom;

public class CustomTargetNotFoundException extends CustomGenericException {

    public CustomTargetNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
