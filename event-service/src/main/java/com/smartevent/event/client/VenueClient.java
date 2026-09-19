package com.smartevent.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "VENUE-SERVICE")
public interface VenueClient {

    @GetMapping("/api/venues/{venueId}/capacity")
    Integer getVenueCapacity(
            @PathVariable("venueId") Long venueId
    );
}
