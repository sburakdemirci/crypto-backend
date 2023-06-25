package com.mtd.crypto.core.security;

import com.mtd.crypto.core.security.exception.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerCustom
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationFailure(
            AuthenticationException ex, WebRequest request) {
        String bodyOfResponse = "Failed to authenticate";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }


}