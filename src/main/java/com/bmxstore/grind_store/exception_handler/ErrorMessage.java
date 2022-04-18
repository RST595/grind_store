package com.bmxstore.grind_store.exception_handler;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {

    NOT_EMPTY("Not empty"),
    CATEGORY_NOT_EXIST("Category doesn't exist"),
    PRODUCT_NOT_EXIST("Product doesn't exist"),
    USER_NOT_EXIST("User doesn't exist"),
    EMAIL_IS_ALREADY_TAKEN("Email is already taken"),
    USER_ID_NOT_FOUND("User wasn't founded"),
    BAD_REQUEST("Bad request"),
    NOT_FOUND("Not found"),
    CART_ITEM_NOT_FOUND("Cart item not found"),
    ORDER_NOT_FOUND("Order not found"),
    SERVER_ERROR("Server error"),
    DUPLICATED("Already exits"),
    DUPLICATED_EMAIL("e-mail already exists"),
    USER_PASSWORD_FAIL("Passwords not the same"),
    WRONG_EMAIL("e-mail is not valid"),
    RATES_WASNT_FOUNDED("Fail to get currency rates");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "HttpErrorMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}