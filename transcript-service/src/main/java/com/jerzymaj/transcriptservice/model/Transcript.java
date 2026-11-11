package com.jerzymaj.transcriptservice.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transcript {

    private Long id;

    private String uploadId;

    private String assemblyAiTranscriptId;

    private TranscriptStatus status;

    private String text;

    private LocalDateTime createdAt;
}
