package com.bmxstore.grind_store.dto;

import java.time.LocalDateTime;

public class ServerResponseDTO {

    private final boolean isSuccess;
    private final String message;


    public ServerResponseDTO(boolean success, String message) {
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

