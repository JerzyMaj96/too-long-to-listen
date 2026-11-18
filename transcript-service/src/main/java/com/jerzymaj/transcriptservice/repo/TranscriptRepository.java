package com.jerzymaj.transcriptservice.repo;

import com.jerzymaj.transcriptservice.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
}
