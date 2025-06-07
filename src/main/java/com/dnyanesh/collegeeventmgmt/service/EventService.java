package com.dnyanesh.collegeeventmgmt.service;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.model.Event;
import com.dnyanesh.collegeeventmgmt.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Value("${event.images.upload-dir:uploads/event-images/}")
    private String uploadDir;

    public String saveEventImage(Long eventId, MultipartFile file) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = eventId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            event.setImagePath(fileName);
            eventRepository.save(event);
            // Return API url for image
            return "/api/events/image/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public EventDto createEvent(EventDto dto) {
        Event event = Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .venue(dto.getVenue())
                .capacity(dto.getCapacity())
                .organizer(dto.getOrganizer())
                .approved(dto.isApproved())
                .build();
        event = eventRepository.save(event);
        return toDto(event);
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public EventDto getEvent(Long id) {
        return eventRepository.findById(id).map(this::toDto).orElse(null);
    }

    public EventDto updateEvent(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setVenue(dto.getVenue());
        event.setCapacity(dto.getCapacity());
        event.setOrganizer(dto.getOrganizer());
        event.setApproved(dto.isApproved());
        return toDto(eventRepository.save(event));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .venue(event.getVenue())
                .capacity(event.getCapacity())
                .organizer(event.getOrganizer())
                .approved(event.isApproved())
                .imageUrl(event.getImagePath() != null ? "/api/events/image/" + event.getImagePath() : null)
                .build();
    }

    public Event getEventEntity(Long id) {
        return eventRepository.findById(id).orElse(null);
    }
}