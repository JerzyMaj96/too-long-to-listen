package com.jerzymaj.summaryservice.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timestamp, String description, String details) {
}
