package com.jerzymaj.transcriptservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SentencesResponse(String id,
        List<Sentence> sentences) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sentence(String text, int start, int end) {
    }
}
