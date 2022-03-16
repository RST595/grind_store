package com.bmxstore.grind_store.response_api;

import java.time.LocalDateTime;

public class ResponseApi {

    private final boolean isSuccess;
    private final String message;


    public ResponseApi(boolean success, String message) {
        this.isSuccess = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return LocalDateTime.now().toString();
    }
}

