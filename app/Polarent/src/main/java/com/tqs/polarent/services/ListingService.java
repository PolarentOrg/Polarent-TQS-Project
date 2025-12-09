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
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;

    public List<ListingResponseDTO> getEnabledListings() {
        return listingRepository.findByEnabledTrue().stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<ListingResponseDTO> searchListings(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // Se não houver , retorna todos os ativos
            return getEnabledListings();
        }
        List<Listing> listings = listingRepository.searchByTerm(searchTerm.trim());
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }
}
