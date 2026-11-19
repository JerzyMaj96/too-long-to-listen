package com.jerzymaj.transcriptservice.service;

import com.jerzymaj.transcriptservice.event.TranscriptCompletedEvent;
import com.jerzymaj.transcriptservice.exception.TranscriptNotFoundException;
import com.jerzymaj.transcriptservice.kafka.TranscriptEventProducer;
import com.jerzymaj.transcriptservice.mapper.TranscriptMapper;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import com.jerzymaj.transcriptservice.repo.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptService {

    public final TranscriptRepository transcriptRepository;
    public final TranscriptProvider transcriptProvider;
    public final TranscriptEventProducer producer;

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
            transcript.setSentences(new ArrayList<>(result.getSentences()));
            transcript.setAssemblyAiTranscriptId(result.getAssemblyAiTranscriptId());
        } catch (Exception ex) {
            log.error("Transcription failed for id: {}", transcriptId, ex);
            transcript.setStatus(TranscriptStatus.FAILED);
        }

        transcriptRepository.save(transcript);

        if (transcript.getStatus() == TranscriptStatus.COMPLETED) {
            TranscriptCompletedEvent event = TranscriptMapper.toEvent(transcript);
            producer.send(event);
        }
    }

    public Transcript getTranscriptById(Long transcriptId) {

        return transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new TranscriptNotFoundException("Transcript not found with id: " + transcriptId));
    }
}
