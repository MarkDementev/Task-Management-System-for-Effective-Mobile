package com.tms.exception.handler;

import com.tms.exception.AdminDeletionForbiddenException;
import com.tms.exception.EntityWithIDNotFoundException;

import org.springdoc.api.ErrorMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityWithIDNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityWithIDNotFoundExceptionHandler(EntityWithIDNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(AdminDeletionForbiddenException.class)
    public ResponseEntity<ErrorMessage> adminDeletionForbiddenExceptionHandler(AdminDeletionForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> runtimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> exceptionHandler(Exception e) {
        String separator = " / ";
        String errorMessage = Arrays.stream(e.getStackTrace()).findFirst().orElseThrow()
                + separator + e.getClass().getName()
                + separator + e.getMessage();

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ErrorMessage(errorMessage));
    }
}
