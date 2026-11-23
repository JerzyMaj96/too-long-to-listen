package com.jerzymaj.transcriptservice.integration;

import com.jerzymaj.transcriptservice.event.TranscriptCompletedEvent;
import com.jerzymaj.transcriptservice.kafka.TranscriptEventProducer;
import com.jerzymaj.transcriptservice.model.Sentence;
import com.jerzymaj.transcriptservice.model.Transcript;
import com.jerzymaj.transcriptservice.model.TranscriptStatus;
import com.jerzymaj.transcriptservice.repo.TranscriptRepository;
import com.jerzymaj.transcriptservice.service.TranscriptProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TranscriptControllerIntegrationTests {

    private static final String BASE_URL = "/too-long-to-listen/api/v1/transcripts";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @MockitoBean
    private TranscriptProvider transcriptProvider;

    @MockitoBean
    private TranscriptEventProducer transcriptEventProducer;

    @AfterEach
    void cleanUp() {
        transcriptRepository.deleteAll();
    }

    @Test
    void shouldProcessTranscriptAndPublishEvent() throws Exception {
        Transcript completedTranscript = Transcript.builder()
                .status(TranscriptStatus.COMPLETED)
                .assemblyAiTranscriptId("aai-123")
                .text("Hi, this is test. Second sentence.")
                .sentences(List.of(
                        Sentence.builder().text("Hi, this is test.").startMs(0).endMs(1500).build(),
                        Sentence.builder().text("Second sentence.").startMs(1600).endMs(2800).build()
                ))
                .build();

        when(transcriptProvider.processAudioFile("test.mp3")).thenReturn(completedTranscript);

        mockMvc.perform(post(BASE_URL).param("uploadPath", "test.mp3"))
                .andExpect(status().isAccepted());

        ArgumentCaptor<TranscriptCompletedEvent> captor = ArgumentCaptor.forClass(TranscriptCompletedEvent.class);
        verify(transcriptEventProducer, timeout(5000)).send(captor.capture());

        TranscriptCompletedEvent event = captor.getValue();
        assertThat(event.text()).isEqualTo("Hi, this is test. Second sentence.");
        assertThat(event.sentences()).hasSize(2);
        assertThat(event.sentences().getFirst().startMs()).isZero();

        Transcript saved = transcriptRepository.findAll().getFirst();
        assertThat(saved.getStatus()).isEqualTo(TranscriptStatus.COMPLETED);
        assertThat(saved.getUuid()).isEqualTo(event.transcriptUuid());
    }

    @Test
    void shouldReturnTranscriptById() throws Exception {
        Transcript transcript = transcriptRepository.save(Transcript.builder()
                .status(TranscriptStatus.PROCESSING)
                .build());

        mockMvc.perform(get(BASE_URL + "/{transcriptId}", transcript.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transcript.getId()))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldReturn404_WhenTranscriptNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{transcriptId}", 999L))
                .andExpect(status().isNotFound());
    }
}
