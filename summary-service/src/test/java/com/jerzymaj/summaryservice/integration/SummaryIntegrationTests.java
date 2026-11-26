package com.jerzymaj.summaryservice.integration;

import com.jerzymaj.summaryservice.dto.SentenceResult;
import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import com.jerzymaj.summaryservice.kafka.TranscriptEventConsumer;
import com.jerzymaj.summaryservice.llm.LlmSummary;
import com.jerzymaj.summaryservice.llm.SummaryGenerator;
import com.jerzymaj.summaryservice.repo.SummaryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SummaryIntegrationTests {

    private static final String BASE_URL = "/too-long-to-listen/api/v1/summaries";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TranscriptEventConsumer consumer;

    @Autowired
    private SummaryRepository summaryRepository;

    @MockitoBean
    private SummaryGenerator summaryGenerator;

    @AfterEach
    void cleanUp() {
        summaryRepository.deleteAll();
    }

    private TranscriptCompletedEvent event(UUID transcriptUuid) {
        return new TranscriptCompletedEvent(
                transcriptUuid,
                "Hello, this is a test. Second sentence.",
                List.of(new SentenceResult("Hello, this is a test.", 0, 1500),
                        new SentenceResult("Second sentence.", 1600, 2800)));
    }

    private LlmSummary llmSummary() {
        return new LlmSummary(
                "A short test recording.",
                List.of(new LlmSummary.LlmKeyMoment(0, "Greeting", "The speaker says hello."),
                        new LlmSummary.LlmKeyMoment(1600, "Second point", "The speaker adds a second sentence.")));
    }

    @Test
    void shouldSummarizeEventAndReturnItViaRest() throws Exception {
        UUID transcriptUuid = UUID.randomUUID();
        when(summaryGenerator.generate(any())).thenReturn(llmSummary());

        consumer.consume(event(transcriptUuid));

        mockMvc.perform(get(BASE_URL + "/{transcriptUuid}", transcriptUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transcriptUuid").value(transcriptUuid.toString()))
                .andExpect(jsonPath("$.summary").value("A short test recording."))
                .andExpect(jsonPath("$.keyMoments.length()").value(2))
                .andExpect(jsonPath("$.keyMoments[1].startMs").value(1600))
                .andExpect(jsonPath("$.keyMoments[1].title").value("Second point"));
    }

    @Test
    void shouldNotSummarizeTheSameTranscriptTwice() {
        UUID transcriptUuid = UUID.randomUUID();
        when(summaryGenerator.generate(any())).thenReturn(llmSummary());

        consumer.consume(event(transcriptUuid));
        consumer.consume(event(transcriptUuid));

        verify(summaryGenerator, times(1)).generate(any());
        assertThat(summaryRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturn404_WhenSummaryNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{transcriptUuid}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").exists());
    }
}
