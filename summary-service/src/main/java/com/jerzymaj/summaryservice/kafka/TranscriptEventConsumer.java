package com.jerzymaj.summaryservice.kafka;

import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TranscriptEventConsumer {

    @KafkaListener(topics = "transcript-completed")
    public void consume(TranscriptCompletedEvent event) {
        log.info("Received transcript {} with {} sentences", event.transcriptUuid(), event.sentences().size());
    }
}
