package com.jerzymaj.transcriptservice.event;

import com.jerzymaj.transcriptservice.dto.SentenceResult;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record TranscriptCompletedEvent(UUID transcriptUuid, String text,
                                       List<SentenceResult> sentences) {
}
