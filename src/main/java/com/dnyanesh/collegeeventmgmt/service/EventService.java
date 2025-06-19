package com.dnyanesh.collegeeventmgmt.service;

import com.dnyanesh.collegeeventmgmt.dto.EventDto;
import com.dnyanesh.collegeeventmgmt.model.Event;
import com.dnyanesh.collegeeventmgmt.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
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

    // Save event image with validation and delete old image if updating
    public String saveEventImage(Long eventId, MultipartFile file) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 1. File size validation (max 5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds the maximum allowed: 5MB");
        }

        // 2. Extension validation
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.toLowerCase().endsWith(".jpg")
                || originalFilename.toLowerCase().endsWith(".jpeg")
                || originalFilename.toLowerCase().endsWith(".png"))) {
            throw new RuntimeException("Only JPG and PNG images are allowed.");
        }

        // 3. Content validation
        try {
            if (ImageIO.read(file.getInputStream()) == null) {
                throw new RuntimeException("File is not a valid image.");
            }
        } catch (Exception e) {
            throw new RuntimeException("File is not a valid image.");
        }

        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = eventId + "_" + System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Delete old image if updating
            String oldImage = event.getImagePath();
            if (oldImage != null && !oldImage.isEmpty()) {
                Path oldPath = Paths.get(uploadDir, oldImage);
                try { Files.deleteIfExists(oldPath); } catch (Exception ignored) {}
            }

            event.setImagePath(fileName);
            eventRepository.save(event);
            // Return API url for image
            return "/api/events/image/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public EventDto createEvent(EventDto dto) {
        // Always set approved to false when creating a new event (approval workflow)
        Event event = Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .venue(dto.getVenue())
                .capacity(dto.getCapacity())
                .organizer(dto.getOrganizer())
                .approved(false) // new events are unapproved by default
                .build();
        event = eventRepository.save(event);
        return toDto(event);
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<EventDto> getApprovedEvents() {
        return eventRepository.findAll().stream()
                .filter(Event::isApproved)
                .map(this::toDto)
                .collect(Collectors.toList());
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
        // DO NOT allow normal update to approve event
        // event.setApproved(dto.isApproved());
        return toDto(eventRepository.save(event));
    }

    // Approve event (set approved = true)
    public EventDto approveEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setApproved(true);
        return toDto(eventRepository.save(event));
    }

    // Reject event (set approved = false) -- you can customize to delete or add a rejected status if needed
    public EventDto rejectEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setApproved(false);
        return toDto(eventRepository.save(event));
    }

    // Delete event and its image file if present
    public boolean deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null && event.getImagePath() != null) {
            Path imagePath = Paths.get(uploadDir, event.getImagePath());
            try {
                Files.deleteIfExists(imagePath);
            } catch (Exception ignored) {}
        }
        eventRepository.deleteById(id);

        return true;
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