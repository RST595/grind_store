package com.bmxstore.grind_store.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ServiceError.class})
    protected ResponseEntity<TypicalError> handleException(
            ServiceError serviceError) {
        log.error(serviceError.getMessage(), serviceError);
        TypicalError typicalError = new TypicalError(serviceError.getHttpStatus(), serviceError.getHttpError());
        return new ResponseEntity<>(typicalError, typicalError.getStatus());
    }

    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<TypicalError> handleGeneralException(
            Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        TypicalError typicalError = new TypicalError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SERVER_ERROR);
        return new ResponseEntity<>(typicalError, typicalError.getStatus());
    }
}