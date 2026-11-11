package com.jerzymaj.transcriptservice.repo;

import com.jerzymaj.transcriptservice.model.Transcript;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTranscriptRepository implements TranscriptRepository {

    private final ConcurrentHashMap<Long, Transcript> transcriptMap = new ConcurrentHashMap<>();

    @Override
    public void save(Transcript transcript) {
        transcriptMap.put(transcript.getId(), transcript);
    }

    @Override
    public Optional<Transcript> findById(Long transcriptId) {
        return Optional.ofNullable(transcriptMap.get(transcriptId));
    }
}
