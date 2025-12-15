package com.tqs.polarent.services;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.dto.RequestRequestDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Booking;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.mapper.RequestMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final ListingRepository listingRepository;

    public List<RequestResponseDTO> getRequestsByListing(Long listingId) {
        return requestRepository.findByListingId(listingId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RequestResponseDTO> getRequestsByRequester(Long requesterId) {
        return requestRepository.findByRequesterId(requesterId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RequestResponseDTO getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        return requestMapper.toDto(request);
    } // <--- Add this line

    @Transactional
    public RequestResponseDTO createRequest(RequestRequestDTO dto) {
        Request request = requestMapper.toEntity(dto);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    public List<RequestResponseDTO> createBatchRequests(List<RequestRequestDTO> dtos) {
        if (dtos.isEmpty()) {
            throw new IllegalArgumentException("Request list cannot be empty");
        }

        Long requesterId = dtos.get(0).getRequesterId();
        Set<Long> listingIds = new HashSet<>();

        // First pass: validate requester consistency and check for duplicates
        for (RequestRequestDTO dto : dtos) {
            if (!requesterId.equals(dto.getRequesterId())) {
                throw new IllegalArgumentException("All requests must have the same requester");
            }
            if (!listingIds.add(dto.getListingId())) {
                throw new IllegalArgumentException("Duplicate listing ID in batch: " + dto.getListingId());
            }
        }

        // Second pass: validate listings exist and ownership
        for (RequestRequestDTO dto : dtos) {
            Listing listing = listingRepository.findById(dto.getListingId())
                    .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + dto.getListingId()));
            if (listing.getOwnerId().equals(requesterId)) {
                throw new IllegalArgumentException("Cannot request your own listing: " + dto.getListingId());
            }
        }

        return dtos.stream()
                .map(dto -> requestMapper.toDto(requestRepository.save(requestMapper.toEntity(dto))))
                .toList();
    }

    public List<RequestResponseDTO> getRequestsByListingAndRequester(Long listingId, Long requesterId) {
        return requestRepository.findByListingIdAndRequesterId(listingId, requesterId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new IllegalArgumentException("Request not found");
        }
        requestRepository.deleteById(id);
    }

}