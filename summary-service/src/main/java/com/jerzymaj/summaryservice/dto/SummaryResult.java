package com.jerzymaj.summaryservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SummaryResult(UUID transcriptUuid, String summary, List<KeyMomentResult> keyMoments,
                            LocalDateTime createdAt) {

    public record KeyMomentResult(int startMs, String title, String description) {
    }
}
