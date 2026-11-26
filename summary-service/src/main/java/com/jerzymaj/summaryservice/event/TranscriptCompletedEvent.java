package com.jerzymaj.summaryservice.event;

import com.jerzymaj.summaryservice.dto.SentenceResult;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record TranscriptCompletedEvent(UUID transcriptUuid, String text,
                                       List<SentenceResult> sentences) {
}
