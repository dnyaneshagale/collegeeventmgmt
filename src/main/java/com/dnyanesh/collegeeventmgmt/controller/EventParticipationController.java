package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.dto.EventRegistrationDto;
import com.dnyanesh.collegeeventmgmt.exception.ResourceNotFoundException;
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
@RequestMapping("/api/participation/events")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService participationService;

    @GetMapping
    public ResponseEntity<List<EventDto>> listEvents(@RequestParam(required = false) String filter) {
        return ResponseEntity.ok(participationService.listEvents(filter));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        EventDto event = participationService.getEventDetails(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        return ResponseEntity.ok(event);
    }

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

    @GetMapping("/registered")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY')")
    public ResponseEntity<List<EventRegistrationDto>> getRegisteredEvents(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                participationService.getRegisteredEvents(userDetails.getUsername())
        );
    }

    @PostMapping("/{eventId}/feedback")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY')")
    public ResponseEntity<EventRegistrationDto> submitFeedback(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String feedback = payload != null ? payload.get("feedback") : null;
        return ResponseEntity.ok(
                participationService.submitFeedback(eventId, userDetails.getUsername(), feedback)
        );
    }
}