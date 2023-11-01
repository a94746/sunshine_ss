package com.vindie.sunshine_ss.common.config;

import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SunshineException.class)
    protected ResponseEntity<Object> sunshineException(RuntimeException ex, WebRequest request) {
        SunshineException sunshineException = (SunshineException) ex;
        return handleExceptionInternal(ex, sunshineException.getUiExceprion().toString(),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> usernameNotFoundException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
}