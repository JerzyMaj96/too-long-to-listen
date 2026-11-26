package com.jerzymaj.summaryservice.service;

import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import com.jerzymaj.summaryservice.exception.SummaryNotFoundException;
import com.jerzymaj.summaryservice.llm.LlmSummary;
import com.jerzymaj.summaryservice.llm.SummaryGenerator;
import com.jerzymaj.summaryservice.mapper.SummaryMapper;
import com.jerzymaj.summaryservice.model.Summary;
import com.jerzymaj.summaryservice.repo.SummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final SummaryGenerator summaryGenerator;

    public void summarize(TranscriptCompletedEvent event) {
        if (summaryRepository.existsByTranscriptUuid(event.transcriptUuid())) {
            log.info("Summary for transcript {} already exists, skipping", event.transcriptUuid());
            return;
        }

        LlmSummary llmSummary = summaryGenerator.generate(event);

        Summary summary = SummaryMapper.toEntity(event.transcriptUuid(), llmSummary);
        summaryRepository.save(summary);

        log.info("Saved summary for transcript {} with {} key moments",
                event.transcriptUuid(), summary.getKeyMoments().size());
    }

    public Summary getByTranscriptUuid(UUID transcriptUuid) {
        return summaryRepository.findByTranscriptUuid(transcriptUuid)
                .orElseThrow(() -> new SummaryNotFoundException("Summary not found for transcript: " + transcriptUuid));
    }
}
