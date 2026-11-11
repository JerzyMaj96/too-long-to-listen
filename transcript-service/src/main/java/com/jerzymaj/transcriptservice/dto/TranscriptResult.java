package com.jerzymaj.transcriptservice.dto;

import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TranscriptResult(Long id, String uploadId, String assemblyAiTranscriptId, TranscriptStatus status,
                               String text,
                               LocalDateTime createdAt) {
}
