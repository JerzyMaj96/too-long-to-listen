package com.jerzymaj.transcriptservice.repo;

import com.jerzymaj.transcriptservice.model.Transcript;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTranscriptRepository implements TranscriptRepository {

    private final ConcurrentHashMap<Long, Transcript> transcriptMap = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public void save(Transcript transcript) {
        if (transcript.getId() == null) {
            transcript.setId(idGenerator.incrementAndGet());
        }
        transcriptMap.put(transcript.getId(), transcript);
    }

    @Override
    public Optional<Transcript> findById(Long transcriptId) {
        return Optional.ofNullable(transcriptMap.get(transcriptId));
    }
}
