package com.jerzymaj.summaryservice.llm;

import com.jerzymaj.summaryservice.event.TranscriptCompletedEvent;

/**
 * Port: turns a transcript into a summary with key moments. The LLM provider hides behind it.
 */
public interface SummaryGenerator {

    LlmSummary generate(TranscriptCompletedEvent transcript);
}
