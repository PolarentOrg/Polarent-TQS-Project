package com.tqs.polarent.controller;
import com.tqs.polarent.dto.*;
import com.tqs.polarent.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tqs.polarent.entity.Listing;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @GetMapping
    public ResponseEntity<List<ListingResponseDTO>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<ListingResponseDTO>> getEnabledListings() {
        return ResponseEntity.ok(listingService.getEnabledListings());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ListingResponseDTO>> getListingsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(listingService.getListingsByOwner(ownerId));
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
    @DeleteMapping("/{userId}/{listingId}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long userId,
            @PathVariable Long listingId) {
        listingService.deleteListing(userId, listingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
