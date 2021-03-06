package com.bmxstore.grind_store.exception_handler;

import org.springframework.http.HttpStatus;


public class ServiceError extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorMessage httpError;

    public ServiceError(HttpStatus httpStatus, ErrorMessage httpError) {
        super(httpError.getMessage());
        this.httpStatus = httpStatus;
        this.httpError = httpError;
    }

    public ErrorMessage getHttpError() {
        return httpError;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}