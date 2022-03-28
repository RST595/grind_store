package com.bmxstore.grind_store.ex_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ServiceExHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ServiceError.class})
    protected ResponseEntity<ApiError> handleException(
            ServiceError serviceError) {
        log.error(serviceError.getMessage(), serviceError);
        ApiError apiError = new ApiError(serviceError.getHttpStatus(), serviceError.getHttpError());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<ApiError> handleGeneralException(
            Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SERVER_ERROR);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}