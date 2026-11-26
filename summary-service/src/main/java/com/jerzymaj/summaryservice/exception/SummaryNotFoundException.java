package com.jerzymaj.summaryservice.exception;

public class SummaryNotFoundException extends RuntimeException {

    public SummaryNotFoundException(String message) {
        super(message);
    }
}
