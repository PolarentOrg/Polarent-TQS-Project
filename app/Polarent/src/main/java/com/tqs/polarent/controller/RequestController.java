package com.tqs.polarent.controller;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.services.BookingService;
import com.tqs.polarent.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final BookingService bookingService;
    private final ListingRepository listingRepository;

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByListing(@PathVariable Long listingId) {
        List<RequestResponseDTO> requests = requestService.getRequestsByListing(listingId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/accept")
    public ResponseEntity<BookingResponseDTO> convertToBooking(@RequestBody RequestResponseDTO requestDto) {
        Listing listing = listingRepository.findById(requestDto.getListingId())
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        BookingRequestDTO bookingRequest = new BookingRequestDTO();
        bookingRequest.setRequestId(requestDto.getId());
        bookingRequest.setPrice(listing.getDailyRate() * requestDto.getDuration());

        return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
    }
}
