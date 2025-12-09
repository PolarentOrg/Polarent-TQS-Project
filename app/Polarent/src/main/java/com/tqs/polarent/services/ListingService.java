package com.tqs.polarent.services;

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
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;

    public List<ListingResponseDTO> getEnabledListings() {
        return listingRepository.findByEnabledTrue().stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

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
