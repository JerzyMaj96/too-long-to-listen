package com.jerzymaj.transcriptservice.mapper;

import com.jerzymaj.transcriptservice.dto.TranscriptResult;
import com.jerzymaj.transcriptservice.model.Transcript;

public final class TranscriptMapper {

    private TranscriptMapper() {
    }

    public static TranscriptResult toDto(Transcript transcript) {

        return TranscriptResult.builder()
                .id(transcript.getId())
                .uploadId(transcript.getUploadId())
                .assemblyAiTranscriptId(transcript.getAssemblyAiTranscriptId())
                .status(transcript.getStatus())
                .text(transcript.getText())
                .createdAt(transcript.getCreatedAt())
                .build();
    }
}
