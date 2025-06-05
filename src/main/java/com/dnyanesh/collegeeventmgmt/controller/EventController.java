package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.entity.Event;
import com.dnyanesh.collegeeventmgmt.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.saveEvent(event);
    }
}
