package com.jerzymaj.summaryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "key_moments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyMoment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int startMs;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}
