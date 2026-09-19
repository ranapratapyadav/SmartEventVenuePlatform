package com.smartevent.venue.service;


import org.springframework.stereotype.Service;

import com.smartevent.venue.entity.Venue;
import com.smartevent.venue.repository.VenueRepository;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    // CREATE
    public Venue createVenue(Venue venue) {

        boolean exists = venueRepository.existsByVenueNameAndCity(
                venue.getVenueName(),
                venue.getCity()
        );

        if (exists) {
            throw new RuntimeException(
                    "Venue with this name already exists in this city"
            );
        }

        if (venue.getStatus() == null) {
            venue.setStatus(
            		com.smartevent.venue.entity.VenueStatus.ACTIVE
            );
        }

        return venueRepository.save(venue);
    }

    // GET ALL
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    // GET BY ID
    public Venue getVenueById(Long venueId) {

        return venueRepository.findById(venueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venue not found with ID: " + venueId
                        )
                );
    }

    // GET BY CITY
    public List<Venue> getVenuesByCity(String city) {
        return venueRepository.findByCityIgnoreCase(city);
    }

    // UPDATE
    public Venue updateVenue(Long venueId, Venue updatedVenue) {

        Venue existingVenue = getVenueById(venueId);

        existingVenue.setVenueName(updatedVenue.getVenueName());
        existingVenue.setCity(updatedVenue.getCity());
        existingVenue.setCapacity(updatedVenue.getCapacity());
        existingVenue.setVenueType(updatedVenue.getVenueType());
        existingVenue.setFacilities(updatedVenue.getFacilities());

        if (updatedVenue.getStatus() != null) {
            existingVenue.setStatus(updatedVenue.getStatus());
        }

        return venueRepository.save(existingVenue);
    }

    // DELETE
    public void deleteVenue(Long venueId) {

        Venue venue = getVenueById(venueId);

        venueRepository.delete(venue);
    }
}
