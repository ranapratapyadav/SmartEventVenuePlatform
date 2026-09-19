package com.smartevent.venue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartevent.venue.entity.Venue;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Optional<Venue> findByVenueNameAndCity(
            String venueName,
            String city
    );

    boolean existsByVenueNameAndCity(
            String venueName,
            String city
    );

    List<Venue> findByCityIgnoreCase(String city);
}