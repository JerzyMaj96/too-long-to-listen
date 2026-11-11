package com.jerzymaj.transcriptservice.service;

import com.jerzymaj.transcriptservice.dto.TranscriptResult;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.repo.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranscriptService {

    public final TranscriptRepository transcriptRepository;

    @Async
    public void processFileAudioTranscription(String folderId) {


    }

    public Transcript getTranscriptById(Long transcriptId) {

        Optional<Transcript> transcript = transcriptRepository.findById(transcriptId);

        if (transcript == null) {
            throw new RuntimeException("Transcript not found");
        }

        return transcript.get();
    }
}
