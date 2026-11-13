package com.jerzymaj.transcriptservice.exception;

public class TranscriptNotFoundException extends RuntimeException {
    public TranscriptNotFoundException(String message) {
        super(message);
    }
}
