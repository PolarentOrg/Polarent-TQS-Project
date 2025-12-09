package com.tqs.polarent.controller;
import com.tqs.polarent.dto.*;
import com.tqs.polarent.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tqs.polarent.entity.Listing;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @GetMapping("/enabled")
    public ResponseEntity<List<ListingResponseDTO>> getEnabledListings() {
        return ResponseEntity.ok(listingService.getEnabledListings());
    }

    @PostMapping
    public ListingResponseDTO createListing(@RequestBody @Valid
    ListingRequestDTO dto) {
        return listingService.createListing(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> updateListing(@PathVariable Long id, @RequestBody ListingResponseDTO dto) {
        return ResponseEntity.ok(listingService.updateListing(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> patchListing(@PathVariable Long id, @RequestBody ListingResponseDTO dto) {
        return ResponseEntity.ok(listingService.patchListing(id, dto));
    }
}
