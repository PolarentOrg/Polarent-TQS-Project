package com.tqs.polarent.controller;

import com.tqs.polarent.service.ListingService;
import org.springframework.web.bind.annotation.*;
import com.tqs.polarent.entity.Listing;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<Listing> getAllBookings() {
        return listingService.findAll();
    }

    @PostMapping("/add")
    public Listing addListing(@RequestBody @Valid Listing listing) {
        return listingService.save(listing);
    }
}
