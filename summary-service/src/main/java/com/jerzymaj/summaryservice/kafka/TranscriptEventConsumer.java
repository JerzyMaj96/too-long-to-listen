package com.jerzymaj.summaryservice.kafka;

import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import com.jerzymaj.summaryservice.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptEventConsumer {

    private final SummaryService summaryService;

    @KafkaListener(topics = "transcript-completed")
    public void consume(TranscriptCompletedEvent event) {
        log.info("Received transcript {} with {} sentences", event.transcriptUuid(), event.sentences().size());
        summaryService.summarize(event);
    }
}
