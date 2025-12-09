package com.tqs.polarent.service;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;

    public List<ListingResponseDTO> getEnabledListings() {
        return listingRepository.findByEnabledTrue()
                .stream()
                .map(listingMapper::toDto)
                .toList();
    }

    @Transactional
    public ListingResponseDTO createListing(ListingRequestDTO dto) {
        if (!userRepository.existsById(dto.getOwnerId())) {
            throw new IllegalArgumentException("Owner not found");
        }

        Listing listing = Listing.builder()
                .ownerId(dto.getOwnerId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dailyRate(dto.getDailyRate())
                .enabled(dto.getEnabled())
                .build();

        return listingMapper.toDto(
                listingRepository.save(listing)
        );
    }

}
