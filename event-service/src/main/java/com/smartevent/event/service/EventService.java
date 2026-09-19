package com.smartevent.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartevent.event.client.VenueClient;
import com.smartevent.event.entity.Event;
import com.smartevent.event.entity.EventStatus;
import com.smartevent.event.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueClient venueClient;

    public EventService(
            EventRepository eventRepository,
            VenueClient venueClient) {

        this.eventRepository = eventRepository;
        this.venueClient = venueClient;
    }

    // CREATE EVENT
    public Event createEvent(Event event) {

        Integer venueCapacity =
                venueClient.getVenueCapacity(event.getVenueId());

        if (event.getTotalSeats() > venueCapacity) {

            throw new RuntimeException(
                    "Event seats cannot exceed venue capacity. "
                    + "Venue capacity: " + venueCapacity
            );
        }

        event.setAvailableSeats(event.getTotalSeats());

        event.setStatus(EventStatus.UPCOMING);

        return eventRepository.save(event);
    }

    // GET ALL EVENTS
    public List<Event> getAllEvents() {

        return eventRepository.findAll();
    }

    // GET EVENT BY ID
    public Event getEventById(Long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found with ID: "
                                + eventId
                        )
                );
    }

    // GET EVENTS BY VENUE
    public List<Event> getEventsByVenue(Long venueId) {

        return eventRepository.findByVenueId(venueId);
    }

    // GET EVENTS BY CATEGORY
    public List<Event> getEventsByCategory(
    		com.smartevent.event.entity.EventCategory category) {

        return eventRepository.findByEventCategory(category);
    }

    // UPDATE EVENT
    public Event updateEvent(
            Long eventId,
            Event updatedEvent) {

        Event existingEvent = getEventById(eventId);

        Integer venueCapacity =
                venueClient.getVenueCapacity(
                        updatedEvent.getVenueId()
                );

        if (updatedEvent.getTotalSeats() > venueCapacity) {

            throw new RuntimeException(
                    "Event seats cannot exceed venue capacity"
            );
        }

        existingEvent.setEventName(
                updatedEvent.getEventName()
        );

        existingEvent.setEventCategory(
                updatedEvent.getEventCategory()
        );

        existingEvent.setEventDate(
                updatedEvent.getEventDate()
        );

        existingEvent.setEventTime(
                updatedEvent.getEventTime()
        );

        existingEvent.setVenueId(
                updatedEvent.getVenueId()
        );

        existingEvent.setTotalSeats(
                updatedEvent.getTotalSeats()
        );

        existingEvent.setAvailableSeats(
                updatedEvent.getTotalSeats()
        );

        existingEvent.setTicketPrice(
                updatedEvent.getTicketPrice()
        );

        if (updatedEvent.getStatus() != null) {
            existingEvent.setStatus(
                    updatedEvent.getStatus()
            );
        }

        return eventRepository.save(existingEvent);
    }

    // DELETE
    public void deleteEvent(Long eventId) {

        Event event = getEventById(eventId);

        eventRepository.delete(event);
    }
}
