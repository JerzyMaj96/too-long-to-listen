package com.jerzymaj.transcriptservice.kafka;

import com.jerzymaj.transcriptservice.event.TranscriptCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranscriptEventProducer {

    private final KafkaTemplate<String, TranscriptCompletedEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(TranscriptCompletedEvent event) {
        kafkaTemplate.send(topic, event.transcriptUuid().toString(), event);
    }
}
