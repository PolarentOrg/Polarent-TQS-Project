package com.tqs.polarent.controller;
import com.tqs.polarent.dto.*;
import com.tqs.polarent.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @GetMapping("/enabled")
    public ResponseEntity<List<ListingResponseDTO>> getEnabledListings() {
        List<ListingResponseDTO> listings = listingService.getEnabledListings();
        return ResponseEntity.ok(listings);
    }
}