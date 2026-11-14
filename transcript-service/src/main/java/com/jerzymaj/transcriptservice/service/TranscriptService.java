package com.jerzymaj.transcriptservice.service;

import com.jerzymaj.transcriptservice.exception.TranscriptNotFoundException;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import com.jerzymaj.transcriptservice.repo.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptService {

    public final TranscriptRepository transcriptRepository;
    public final TranscriptProvider transcriptProvider;

    public Transcript createTranscript() {
        Transcript transcript = Transcript.builder()
                .status(TranscriptStatus.PROCESSING)
                .build();

        transcriptRepository.save(transcript);
        return transcript;
    }

    @Async
    public void processTranscriptAsync(Long transcriptId, String uploadPath) {
        Transcript transcript = getTranscriptById(transcriptId);

        try {
            Transcript result = transcriptProvider.processAudioFile(uploadPath);
            transcript.setStatus(result.getStatus());
            transcript.setText(result.getText());
            transcript.setAssemblyAiTranscriptId(result.getAssemblyAiTranscriptId());
        } catch (Exception ex) {
            log.error("Transcription failed for id: {}", transcriptId, ex);
            transcript.setStatus(TranscriptStatus.FAILED);
        }

        transcriptRepository.save(transcript);
    }

    public Transcript getTranscriptById(Long transcriptId) {

        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new TranscriptNotFoundException("Transcript not found with id: " + transcriptId));

        return transcript;
    }
}
