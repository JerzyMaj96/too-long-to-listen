package com.jerzymaj.transcriptservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transcripts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Builder.Default
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    private String uploadId;

    private String assemblyAiTranscriptId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private TranscriptStatus status;

    @Column(columnDefinition = "TEXT")
    private String text;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "transcript_id", nullable = false)
    @Builder.Default
    private List<Sentence> sentences = new ArrayList<>();
}
