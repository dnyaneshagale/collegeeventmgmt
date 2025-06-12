package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.service.EventService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<EventDto>> getApprovedEvents() {
        return ResponseEntity.ok(eventService.getApprovedEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        EventDto event = eventService.getEvent(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        return ResponseEntity.ok(eventService.createEvent(eventDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto) {
        try {
            EventDto updated = eventService.updateEvent(id, eventDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/image")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public ResponseEntity<?> uploadEventImage(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = eventService.saveEventImage(eventId, file);
        return ResponseEntity.ok().body(java.util.Map.of("imageUrl", imageUrl));
    }

    @GetMapping("/image/{filename:.+}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public ResponseEntity<Resource> getEventImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/event-images/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Approve event (admin/faculty only)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<EventDto> approveEvent(@PathVariable Long id) {
        try {
            EventDto approved = eventService.approveEvent(id);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Reject event (admin/faculty only)
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<EventDto> rejectEvent(@PathVariable Long id) {
        try {
            EventDto rejected = eventService.rejectEvent(id);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}