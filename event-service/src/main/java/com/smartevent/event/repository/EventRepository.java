package com.smartevent.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartevent.event.entity.Event;
import com.smartevent.event.entity.EventCategory;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByVenueId(Long venueId);

    List<Event> findByEventCategory(EventCategory eventCategory);

    List<Event> findByEventDate(LocalDate eventDate);
}