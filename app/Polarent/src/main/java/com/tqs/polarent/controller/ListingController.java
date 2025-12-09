package com.tqs.polarent.controller;
import com.tqs.polarent.dto.*;
import com.tqs.polarent.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.tqs.polarent.entity.Listing;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @GetMapping
    public List<ListingResponseDTO> getEnabledListings() {
        return listingService.getEnabledListings();
    }

    @PostMapping
    public ListingResponseDTO createListing(
            @RequestBody @Valid ListingRequestDTO dto) {
        return listingService.createListing(dto);
    }

    @DeleteMapping("/{userId}/{listingId}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long userId,
            @PathVariable Long listingId) {
        listingService.deleteListing(userId, listingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
