package com.jerzymaj.summaryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler(SummaryNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleSummaryNotFound(SummaryNotFoundException ex, WebRequest request) {
        return build(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobal(Exception ex, WebRequest request) {
        return build(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetails> build(Exception ex, WebRequest request, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)), status);
    }
}
