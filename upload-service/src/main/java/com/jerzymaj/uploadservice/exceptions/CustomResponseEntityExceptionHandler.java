package com.jerzymaj.uploadservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<ErrorDetails> handleFileEmptyException(FileEmptyException ex, WebRequest request) {

        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FiletypeNotAllowedException.class)
    public ResponseEntity<ErrorDetails> handleFiletypeNotAllowedException(FiletypeNotAllowedException ex, WebRequest request) {

        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {

        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, status);
    }
}
