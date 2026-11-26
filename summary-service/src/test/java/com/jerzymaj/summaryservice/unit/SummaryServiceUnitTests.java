package com.jerzymaj.summaryservice.unit;

import com.jerzymaj.summaryservice.dto.SentenceResult;
import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import com.jerzymaj.summaryservice.exception.SummaryNotFoundException;
import com.jerzymaj.summaryservice.llm.LlmSummary;
import com.jerzymaj.summaryservice.llm.SummaryGenerator;
import com.jerzymaj.summaryservice.model.Summary;
import com.jerzymaj.summaryservice.repo.SummaryRepository;
import com.jerzymaj.summaryservice.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceUnitTests {

    @Mock
    private SummaryRepository summaryRepository;

    @Mock
    private SummaryGenerator summaryGenerator;

    @InjectMocks
    private SummaryService summaryService;

    private final UUID transcriptUuid = UUID.randomUUID();

    private final TranscriptCompletedEvent event = new TranscriptCompletedEvent(
            transcriptUuid,
            "Hello, this is a test. Second sentence.",
            List.of(new SentenceResult("Hello, this is a test.", 0, 1500),
                    new SentenceResult("Second sentence.", 1600, 2800)));

    @Test
    void shouldGenerateAndSaveSummary() {
        when(summaryRepository.existsByTranscriptUuid(transcriptUuid)).thenReturn(false);
        when(summaryGenerator.generate(event)).thenReturn(new LlmSummary(
                "A short test recording.",
                List.of(new LlmSummary.LlmKeyMoment(1600, "Second point", "The speaker adds a second sentence."))));

        summaryService.summarize(event);

        ArgumentCaptor<Summary> captor = ArgumentCaptor.forClass(Summary.class);
        verify(summaryRepository).save(captor.capture());
        Summary saved = captor.getValue();
        assertThat(saved.getTranscriptUuid()).isEqualTo(transcriptUuid);
        assertThat(saved.getSummaryText()).isEqualTo("A short test recording.");
        assertThat(saved.getKeyMoments()).hasSize(1);
        assertThat(saved.getKeyMoments().getFirst().getStartMs()).isEqualTo(1600);
    }

    @Test
    void shouldSkip_WhenSummaryAlreadyExists() {
        when(summaryRepository.existsByTranscriptUuid(transcriptUuid)).thenReturn(true);

        summaryService.summarize(event);

        verifyNoInteractions(summaryGenerator);
        verify(summaryRepository, never()).save(any());
    }

    @Test
    void shouldThrow_WhenSummaryNotFound() {
        when(summaryRepository.findByTranscriptUuid(transcriptUuid)).thenReturn(Optional.empty());

        assertThrows(SummaryNotFoundException.class, () -> summaryService.getByTranscriptUuid(transcriptUuid));
    }
}
