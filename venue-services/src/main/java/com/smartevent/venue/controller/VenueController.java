package com.smartevent.venue.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartevent.venue.entity.Venue;
import com.smartevent.venue.service.VenueService;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Venue> createVenue(
            @Valid @RequestBody Venue venue) {

        Venue savedVenue = venueService.createVenue(venue);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedVenue);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {

        return ResponseEntity.ok(
                venueService.getAllVenues()
        );
    }

    // GET BY ID
    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> getVenueById(
            @PathVariable Long venueId) {

        return ResponseEntity.ok(
                venueService.getVenueById(venueId)
        );
    }

    // GET BY CITY
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Venue>> getVenuesByCity(
            @PathVariable String city) {

        return ResponseEntity.ok(
                venueService.getVenuesByCity(city)
        );
    }

    // UPDATE
    @PutMapping("/{venueId}")
    public ResponseEntity<Venue> updateVenue(
            @PathVariable Long venueId,
            @Valid @RequestBody Venue venue) {

        return ResponseEntity.ok(
                venueService.updateVenue(
                        venueId,
                        venue
                )
        );
    }

    // DELETE
    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(
            @PathVariable Long venueId) {

        venueService.deleteVenue(venueId);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{venueId}/capacity")
    public ResponseEntity<Integer> getVenueCapacity(
            @PathVariable Long venueId) {

        Venue venue = venueService.getVenueById(venueId);

        return ResponseEntity.ok(
                venue.getCapacity()
        );
    }
}
