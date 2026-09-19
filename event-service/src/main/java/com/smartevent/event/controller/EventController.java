package com.smartevent.event.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartevent.event.entity.Event;
import com.smartevent.event.entity.EventCategory;
import com.smartevent.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody Event event) {

        Event savedEvent =
                eventService.createEvent(event);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedEvent);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {

        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    // GET BY ID
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.getEventById(eventId)
        );
    }

    // GET BY VENUE
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<Event>> getEventsByVenue(
            @PathVariable Long venueId) {

        return ResponseEntity.ok(
                eventService.getEventsByVenue(venueId)
        );
    }

    // GET BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Event>> getEventsByCategory(
            @PathVariable EventCategory category) {

        return ResponseEntity.ok(
                eventService.getEventsByCategory(category)
        );
    }

    // UPDATE
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody Event event) {

        return ResponseEntity.ok(
                eventService.updateEvent(
                        eventId,
                        event
                )
        );
    }

    // DELETE
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId) {

        eventService.deleteEvent(eventId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
