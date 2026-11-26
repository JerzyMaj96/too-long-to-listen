package com.jerzymaj.summaryservice.llm;

import java.util.List;

/**
 * Shape of the answer we ask the LLM to return (Spring AI maps the JSON onto it).
 */
public record LlmSummary(String summary, List<LlmKeyMoment> keyMoments) {

    public record LlmKeyMoment(int startMs, String title, String description) {
    }
}
