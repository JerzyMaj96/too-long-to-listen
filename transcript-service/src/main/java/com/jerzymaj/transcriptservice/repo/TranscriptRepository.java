package com.jerzymaj.transcriptservice.repo;

import com.jerzymaj.transcriptservice.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
}
