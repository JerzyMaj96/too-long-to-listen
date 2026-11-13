package com.jerzymaj.transcriptservice.exeption;

public class TranscriptNotFoundException extends RuntimeException {
    public TranscriptNotFoundException(String message) {
        super(message);
    }
}
