package com.jerzymaj.transcriptservice.adapter;

import com.jerzymaj.transcriptservice.dto.AssemblyGetResponse;
import com.jerzymaj.transcriptservice.dto.AssemblyUploadResponse;
import com.jerzymaj.transcriptservice.dto.SentencesResponse;
import com.jerzymaj.transcriptservice.dto.TranscriptResponse;
import com.jerzymaj.transcriptservice.mapper.SentenceMapper;
import com.jerzymaj.transcriptservice.model.Sentence;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import com.jerzymaj.transcriptservice.service.TranscriptProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssemblyAiAdapter implements TranscriptProvider {

    private final RestClient restClient;

    @Override
    public Transcript processAudioFile(String uploadPath) throws IOException {

        byte[] audioBytes;

        audioBytes = readUploadFile(uploadPath);

        AssemblyUploadResponse uploadResponse = uploadAudioFile(audioBytes);

        TranscriptResponse transcriptResponse = transcribeAudio(uploadResponse.uploadUrl());

        AssemblyGetResponse getResponse = getTranscript(transcriptResponse.id());

        Transcript transcript = handleErrorWithRetry(getResponse, transcriptResponse);

        return transcript;
    }

    private byte[] readUploadFile(String uploadPath) throws IOException {
        Path path = Path.of(uploadPath);
        return Files.readAllBytes(path);
    }

    private AssemblyUploadResponse uploadAudioFile(byte[] audioBytes) {
        return restClient.post()
                .uri("/upload")
                .body(audioBytes)
                .retrieve()
                .body(AssemblyUploadResponse.class);
    }

    private TranscriptResponse transcribeAudio(String transcriptId) {
        return restClient.post()
                .uri("/transcript")
                .body(Map.of("audio_url", transcriptId, "speech_models", List.of("universal-3-pro", "universal-2")))
                .retrieve()
                .body(TranscriptResponse.class);
    }

    // alternative
    private AssemblyGetResponse getTranscript(String transcriptId) {
        return restClient.get()
                .uri("/transcript/{id}", transcriptId)
                .retrieve()
                .body(AssemblyGetResponse.class);
    }

    private SentencesResponse getSentences(String transcriptId) {
        return restClient.get()
                .uri("/transcript/{id}/sentences", transcriptId)
                .retrieve()
                .body(SentencesResponse.class);
    }

    private Transcript handleErrorWithRetry(AssemblyGetResponse getResponse, TranscriptResponse transcriptResponse) {
        int maxRetries = 120;
        int retryCount = 0;

        while (!"completed".equals(getResponse.status())) {
            if ("error".equals(getResponse.status())) {
                return Transcript.builder()
                        .assemblyAiTranscriptId(transcriptResponse.id())
                        .status(TranscriptStatus.FAILED)
                        .build();
            }

            if (retryCount >= maxRetries) {
                throw new RuntimeException("Max retries reached while waiting for transcript completion");
            }

            retryCount++;

            try {
                Thread.sleep(5000);
                getResponse = getTranscript(transcriptResponse.id());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for transcript completion", ex);
            }
        }

        SentencesResponse sentencesResponse = getSentences(transcriptResponse.id());

        List<Sentence> sentences = sentencesResponse.sentences().stream()
                .map(SentenceMapper::toEntity)
                .toList();

        return Transcript.builder()
                .assemblyAiTranscriptId(transcriptResponse.id())
                .status(TranscriptStatus.COMPLETED)
                .text(getResponse.text())
                .sentences(sentences)
                .build();
    }

}
