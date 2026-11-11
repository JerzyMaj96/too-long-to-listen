package com.jerzymaj.transcriptservice.repo;

import com.jerzymaj.transcriptservice.model.Transcript;

import java.util.Optional;

public interface TranscriptRepository {

    void save(Transcript transcript);

    Optional<Transcript> findById(Long transcriptId);
}
