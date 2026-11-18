package com.jerzymaj.transcriptservice.mapper;

import com.jerzymaj.transcriptservice.dto.SentenceResult;
import com.jerzymaj.transcriptservice.dto.SentencesResponse;
import com.jerzymaj.transcriptservice.model.Sentence;

public final class SentenceMapper {

    private  SentenceMapper() {
    }

    public static Sentence toEntity(SentencesResponse.Sentence sentence) {
        return Sentence.builder()
                .text(sentence.text())
                .startMs(sentence.start())
                .endMs(sentence.end())
                .build();
    }

    public static SentenceResult toDto(Sentence sentence) {
        return new SentenceResult(sentence.getText(), sentence.getStartMs(), sentence.getEndMs());
    }
}
