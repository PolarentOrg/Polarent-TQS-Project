package com.tqs.polarent.controller;

import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByListing(@PathVariable Long listingId) {
        List<RequestResponseDTO> requests = requestService.getRequestsByListing(listingId);
        return ResponseEntity.ok(requests);
    }
}