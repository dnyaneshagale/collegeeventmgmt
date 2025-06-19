package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.exception.ResourceNotFoundException;
import com.dnyanesh.collegeeventmgmt.model.Event;
import com.dnyanesh.collegeeventmgmt.model.EventRegistration;
import com.dnyanesh.collegeeventmgmt.repository.EventRegistrationRepository;
import com.dnyanesh.collegeeventmgmt.service.EventService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventService eventService;
    private final EventRegistrationRepository registrationRepository;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto dto) {
        return ResponseEntity.ok(eventService.createEvent(dto));
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        EventDto event = eventService.getEvent(id);
        if (event == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventDto dto) {
        EventDto updatedEvent = eventService.updateEvent(id, dto);
        if (updatedEvent == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<String>> getRegistrations(@PathVariable Long id) {
        Event event = eventService.getEventEntity(id);
        if (event == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        List<String> participants = registrationRepository.findByEvent(event)
                .stream()
                .map(reg -> reg.getUser().getEmail())
                .collect(Collectors.toList());
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{id}/participants/csv")
    public void downloadParticipantsCsv(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Event event = eventService.getEventEntity(id);
        if (event == null) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        List<EventRegistration> registrations = registrationRepository.findByEvent(event);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=participants.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Name,Email,RegistrationDate");
        for (EventRegistration reg : registrations) {
            writer.printf("%s,%s,%s\n",
                    reg.getUser().getFullName(),
                    reg.getUser().getEmail(),
                    reg.getRegistrationDate());
        }
        writer.flush();
        writer.close();
    }
}