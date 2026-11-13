package com.jerzymaj.transcriptservice.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timeStamp, String description,
                           String details) {
}
