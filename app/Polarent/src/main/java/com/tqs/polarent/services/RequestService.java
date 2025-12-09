package com.tqs.polarent.services;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.dto.RequestRequestDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Booking;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.mapper.RequestMapper;
import com.tqs.polarent.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    public List<RequestResponseDTO> getRequestsByListing(Long listingId) {
        return requestRepository.findByListingId(listingId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RequestResponseDTO> getRequestsByListingAndRequester(Long listingId, Long requesterId) {
        return requestRepository.findByListingAndRequesterId(listingId,requesterId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }


}