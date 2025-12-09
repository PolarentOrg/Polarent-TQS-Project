package com.tqs.polarent.services;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import jakarta.validation.Valid;
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

        Listing listing = listingMapper.toEntity(dto);

        return listingMapper.toDto(
                listingRepository.save(listing)
        );
    }

    public ListingResponseDTO updateListing(Long id, ListingResponseDTO dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setDailyRate(dto.getDailyRate());
        listing.setEnabled(dto.getEnabled());

        return listingMapper.toDto(listingRepository.save(listing));
    }

    public ListingResponseDTO patchListing(Long id, ListingResponseDTO dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (dto.getTitle() != null) listing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) listing.setDescription(dto.getDescription());
        if (dto.getDailyRate() != null) listing.setDailyRate(dto.getDailyRate());
        if (dto.getEnabled() != null) listing.setEnabled(dto.getEnabled());

        return listingMapper.toDto(listingRepository.save(listing));
    @Transactional
    public void deleteListing(Long userId, Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getOwnerId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this listing");
        }

        listingRepository.delete(listing);
    }
}
