package com.jerzymaj.transcriptservice.unit;

import com.jerzymaj.transcriptservice.event.TranscriptCompletedEvent;
import com.jerzymaj.transcriptservice.exception.TranscriptNotFoundException;
import com.jerzymaj.transcriptservice.kafka.TranscriptEventProducer;
import com.jerzymaj.transcriptservice.model.Sentence;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import com.jerzymaj.transcriptservice.repo.TranscriptRepository;
import com.jerzymaj.transcriptservice.service.TranscriptProvider;
import com.jerzymaj.transcriptservice.service.TranscriptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranscriptServiceUnitTests {

    @Mock
    private TranscriptRepository transcriptRepository;

    @Mock
    private TranscriptProvider transcriptProvider;

    @Mock
    private TranscriptEventProducer transcriptEventProducer;

    @InjectMocks
    private TranscriptService transcriptService;

    private Transcript transcript;
    private Transcript completedTranscript;

    @BeforeEach
    public void setup() {
        transcript = Transcript.builder()
                .id(1L)
                .status(TranscriptStatus.PROCESSING)
                .build();

        completedTranscript = Transcript.builder()
                .id(1L)
                .status(TranscriptStatus.COMPLETED)
                .assemblyAiTranscriptId("aai-123")
                .text("Hi, this is test. Second sentence.")
                .sentences(List.of(
                        Sentence.builder().text("Hi, this is test. Second sentence").startMs(0).endMs(1500).build(),
                        Sentence.builder().text("Drugie zdanie.").startMs(1600).endMs(2800).build()
                ))
                .build();
    }

    @Test
    public void processTranscriptAsync_IfSuccess() throws IOException {

        when(transcriptRepository.findById(any())).thenReturn(Optional.of(transcript));
        when(transcriptProvider.processAudioFile("test.mp3")).thenReturn(completedTranscript);

        transcriptService.processTranscriptAsync(transcript.getId(), "test.mp3");

        assertThat(transcript.getStatus()).isEqualTo(TranscriptStatus.COMPLETED);
        verify(transcriptRepository).save(transcript);
        verify(transcriptEventProducer).send(any(TranscriptCompletedEvent.class));
    }

    @Test
    public void processTranscriptAsync_IfFailure() throws IOException {

        when(transcriptRepository.findById(any())).thenReturn(Optional.of(transcript));
        when(transcriptProvider.processAudioFile("test.mp3")).thenThrow(new IOException("AssemblyAI down"));

        transcriptService.processTranscriptAsync(transcript.getId(), "test.mp3");

        assertThat(transcript.getStatus()).isEqualTo(TranscriptStatus.FAILED);
        verify(transcriptEventProducer, never()).send(any());
    }

    @Test
    public void shouldGetTranscriptById_IfSuccess() {

        when(transcriptRepository.findById(transcript.getId())).thenReturn(Optional.ofNullable(transcript));

        Transcript actualResult = transcriptService.getTranscriptById(transcript.getId());

        assertEquals(actualResult, transcript);
    }

    @Test
    public void shouldThrowException_IfFailure() {

        when(transcriptRepository.findById(transcript.getId())).thenReturn(Optional.empty());

        assertThrows(TranscriptNotFoundException.class, () -> transcriptService.getTranscriptById(transcript.getId()));
    }
}
