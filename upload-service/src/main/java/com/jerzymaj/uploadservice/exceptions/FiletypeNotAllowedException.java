package com.jerzymaj.uploadservice.exceptions;

public class FiletypeNotAllowedException extends RuntimeException {
    public FiletypeNotAllowedException(String message) {
        super(message);
    }
}
