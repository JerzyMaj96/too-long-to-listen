package com.jerzymaj.transcriptservice.controller;

import com.jerzymaj.transcriptservice.dto.TranscriptResult;
import com.jerzymaj.transcriptservice.mapper.TranscriptMapper;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/too-long-to-listen/api/v1/transcripts")
@RequiredArgsConstructor
public class TranscriptController {

    public final TranscriptService transcriptService;

    @PostMapping
    public ResponseEntity<String> processFileAudioTranscription(String folderId) {
        transcriptService.processFileAudioTranscription(folderId);
        return ResponseEntity.accepted().body("Request is being processed.");
    }

    @GetMapping("/{transcriptId}")
    public ResponseEntity<TranscriptResult> retrieveTranscriptById(@PathVariable Long transcriptId) {

        Transcript transcript = transcriptService.getTranscriptById(transcriptId);

        TranscriptResult transcriptResult = TranscriptMapper.toDto(transcript);

        return ResponseEntity.ok(transcriptResult);
    }
}
