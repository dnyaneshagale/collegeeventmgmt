package com.dnyanesh.collegeeventmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private User user;

    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

    private String feedback;

    private boolean attended;

    private boolean certificateIssued;
}