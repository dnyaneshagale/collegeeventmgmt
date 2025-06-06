package com.dnyanesh.collegeeventmgmt.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationDto {
    private Long id;
    private Long eventId;
    private String eventName;
    private LocalDateTime registrationDate;
    private String feedback;
    private boolean attended;
    private boolean certificateIssued;
}