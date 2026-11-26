package com.jerzymaj.summaryservice.repo;

import com.jerzymaj.summaryservice.model.Summary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SummaryRepository extends JpaRepository<Summary, Long> {

    boolean existsByTranscriptUuid(UUID transcriptUuid);

    @EntityGraph(attributePaths = "keyMoments")
    Optional<Summary> findByTranscriptUuid(UUID transcriptUuid);
}
