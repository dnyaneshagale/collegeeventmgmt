package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.dto.EventRegistrationDto;
import com.dnyanesh.collegeeventmgmt.service.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participation/events") // <-- CHANGED
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService participationService;

    // List events, with optional filter (?filter=upcoming/past)
    @GetMapping
    public ResponseEntity<List<EventDto>> listEvents(@RequestParam(required = false) String filter) {
        return ResponseEntity.ok(participationService.listEvents(filter));
    }

    // Get event details
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        EventDto event = participationService.getEventDetails(eventId);
        if (event == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(event);
    }

    // Register for event
    @PostMapping("/{eventId}/register")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY')")
    public ResponseEntity<EventRegistrationDto> registerForEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                participationService.registerForEvent(eventId, userDetails.getUsername())
        );
    }

    // Get registered events for current user
    @GetMapping("/registered")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY')")
    public ResponseEntity<List<EventRegistrationDto>> getRegisteredEvents(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                participationService.getRegisteredEvents(userDetails.getUsername())
        );
    }

    // Submit feedback for event
    @PostMapping("/{eventId}/feedback")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY')")
    public ResponseEntity<EventRegistrationDto> submitFeedback(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String feedback = payload.get("feedback");
        return ResponseEntity.ok(
                participationService.submitFeedback(eventId, userDetails.getUsername(), feedback)
        );
    }
}