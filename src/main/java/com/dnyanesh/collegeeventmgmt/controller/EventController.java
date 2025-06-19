package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.exception.ResourceNotFoundException;
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
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        return ResponseEntity.ok(eventService.createEvent(eventDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto) {
        EventDto updatedEvent = eventService.updateEvent(id, eventDto);
        if (updatedEvent == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
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
                throw new ResourceNotFoundException("Image not found with filename: " + filename);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Image not found with filename: " + filename);
        }
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<EventDto> approveEvent(@PathVariable Long id) {
        EventDto approved = eventService.approveEvent(id);
        if (approved == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<EventDto> rejectEvent(@PathVariable Long id) {
        EventDto rejected = eventService.rejectEvent(id);
        if (rejected == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(rejected);
    }
}