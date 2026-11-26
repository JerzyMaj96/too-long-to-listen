package com.jerzymaj.summaryservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "summaries")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Builder.Default
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    @ToString.Include
    private UUID transcriptUuid;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "summary_id", nullable = false)
    @Builder.Default
    private List<KeyMoment> keyMoments = new ArrayList<>();
}
