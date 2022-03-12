package com.bmxstore.grindStore.ExHandler;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class ApiError {

    private HttpStatus status;
    private ErrorMessage message;

    public ApiError(HttpStatus status, ErrorMessage message) {
        this.status = status;
        this.message = message;
    }
}