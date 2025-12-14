package com.tqs.polarent.services;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ListingService.class);

    public ListingResponseDTO getListingById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        return listingMapper.toDto(listing);
    }

    public EquipmentDetailsDTO getEquipmentDetails(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        if (!listing.getEnabled()) {
            throw new IllegalArgumentException("Equipment not available");
        }

        return userRepository.findById(listing.getOwnerId())
                .map(owner -> {
                    EquipmentDetailsDTO details = new EquipmentDetailsDTO();
                    details.setId(listing.getId());
                    details.setTitle(listing.getTitle());
                    details.setDescription(listing.getDescription());
                    details.setDailyRate(listing.getDailyRate());
                    details.setCity(listing.getCity());
                    details.setDistrict(listing.getDistrict());
                    details.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
                    details.setOwnerEmail(owner.getEmail());
                    details.setCreatedAt(listing.getCreatedAt());
                    details.setAvailable(listing.getEnabled());
                    return details;
                })
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
    }

    public List<ListingResponseDTO> getAllListings() {
        return listingRepository.findAll().stream()
                .map(listingMapper::toDto)
                .toList();
    }

    public List<ListingResponseDTO> getEnabledListings() {
        return listingRepository.findByEnabledTrue().stream()
                .map(listingMapper::toDto)
                .toList();
    }

    public List<ListingResponseDTO> getListingsByOwner(Long ownerId) {
        return listingRepository.findByOwnerId(ownerId).stream()
                .map(listingMapper::toDto)
                .toList();
    }

    @Transactional
    public ListingResponseDTO createListing(ListingRequestDTO dto) {
        if (!userRepository.existsById(dto.getOwnerId())) {
            throw new IllegalArgumentException("Owner not found");
        }
        Listing listing = listingMapper.toEntity(dto);
        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponseDTO updateListing(Long id, ListingResponseDTO dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setDailyRate(dto.getDailyRate());
        listing.setEnabled(dto.getEnabled());
        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponseDTO patchListing(Long id, ListingResponseDTO dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        if (dto.getTitle() != null) listing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) listing.setDescription(dto.getDescription());
        if (dto.getDailyRate() != null) listing.setDailyRate(dto.getDailyRate());
        if (dto.getEnabled() != null) listing.setEnabled(dto.getEnabled());
        return listingMapper.toDto(listingRepository.save(listing));
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

    // Search
    public List<ListingResponseDTO> searchListings(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getEnabledListings();
        }
        return listingRepository.searchByTerm(searchTerm.trim()).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    // Filters
    public List<ListingResponseDTO> filterByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        return listingRepository.filterByPriceRange(minPrice, maxPrice).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByMaxPrice(Double maxPrice) {
        if (maxPrice == null || maxPrice <= 0) {
            return getEnabledListings();
        }
        return listingRepository.findByDailyRateLessThanEqualAndEnabledTrue(maxPrice).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByMinPrice(Double minPrice) {
        if (minPrice == null || minPrice < 0) {
            return getEnabledListings();
        }
        return listingRepository.findByDailyRateGreaterThanEqualAndEnabledTrue(minPrice).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return getEnabledListings();
        }
        return listingRepository.findByCityAndEnabledTrue(city.trim()).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> searchByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return getEnabledListings();
        }
        return listingRepository.findByCityContainingIgnoreCase(city.trim()).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            return getEnabledListings();
        }
        return listingRepository.findByDistrictAndEnabledTrue(district.trim()).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByPriceAndCity(Double minPrice, Double maxPrice, String city) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        return listingRepository.filterByPriceAndCity(minPrice, maxPrice, cityParam).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterAdvanced(Double minPrice, Double maxPrice, String city, String district) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String districtParam = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
        return listingRepository.filterAdvanced(minPrice, maxPrice, cityParam, districtParam).stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<String> getAllCities() {
        return listingRepository.findByEnabledTrue().stream()
                .map(Listing::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllDistricts() {
        return listingRepository.findByEnabledTrue().stream()
                .map(Listing::getDistrict)
                .filter(district -> district != null && !district.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeInappropriateListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found with ID: " + id));

        logger.info("[ADMIN ACTION] Removing inappropriate listing:");
        logger.info("  - Listing ID: {}", listing.getId());
        logger.info("  - Title: '{}'", listing.getTitle());
        logger.info("  - Owner ID: {}", listing.getOwnerId());
        logger.info("  - Reason: Inappropriate content");

        listingRepository.delete(listing);
        logger.info("[ADMIN ACTION] Listing successfully removed.");
    }

    public List<ListingResponseDTO> getAllListingsForAdmin() {
        return listingRepository.findAll().stream()
                .map(listingMapper::toDto)
                .toList();
    }
}
