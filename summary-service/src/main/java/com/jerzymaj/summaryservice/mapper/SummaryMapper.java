package com.jerzymaj.summaryservice.mapper;

import com.jerzymaj.summaryservice.dto.SummaryResult;
import com.jerzymaj.summaryservice.llm.LlmSummary;
import com.jerzymaj.summaryservice.model.KeyMoment;
import com.jerzymaj.summaryservice.model.Summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SummaryMapper {

    private SummaryMapper() {
    }

    public static Summary toEntity(UUID transcriptUuid, LlmSummary llmSummary) {
        return Summary.builder()
                .transcriptUuid(transcriptUuid)
                .summaryText(llmSummary.summary())
                .keyMoments(new ArrayList<>(Optional.ofNullable(llmSummary.keyMoments()).orElse(List.of()).stream()
                        .map(SummaryMapper::toEntity)
                        .toList()))
                .build();
    }

    public static SummaryResult toDto(Summary summary) {
        return new SummaryResult(
                summary.getTranscriptUuid(),
                summary.getSummaryText(),
                summary.getKeyMoments().stream()
                        .map(m -> new SummaryResult.KeyMomentResult(m.getStartMs(), m.getTitle(), m.getDescription()))
                        .toList(),
                summary.getCreatedAt());
    }

    private static KeyMoment toEntity(LlmSummary.LlmKeyMoment moment) {
        return KeyMoment.builder()
                .startMs(moment.startMs())
                .title(moment.title())
                .description(moment.description())
                .build();
    }
}
