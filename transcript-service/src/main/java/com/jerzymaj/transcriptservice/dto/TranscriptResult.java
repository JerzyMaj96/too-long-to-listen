package com.jerzymaj.transcriptservice.dto;

import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TranscriptResult(Long id, String uploadId, String assemblyAiTranscriptId, TranscriptStatus status,
                               String text,
                               List<SentenceResult> sentences,
                               LocalDateTime createdAt) {
}
