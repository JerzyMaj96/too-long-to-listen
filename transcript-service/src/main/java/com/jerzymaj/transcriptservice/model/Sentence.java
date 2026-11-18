package com.jerzymaj.transcriptservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transcript_sentences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Builder.Default
    @EqualsAndHashCode.Include
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int startMs;

    @Column(nullable = false)
    private int endMs;
}
