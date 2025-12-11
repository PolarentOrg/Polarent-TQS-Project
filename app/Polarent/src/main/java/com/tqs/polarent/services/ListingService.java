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

    public List<ListingResponseDTO> getAllListings() {
        return listingRepository.findAll()
                .stream()
                .map(listingMapper::toDto)
                .toList();
    }

    public List<ListingResponseDTO> getEnabledListings() {
        return listingRepository.findByEnabledTrue()
                .stream()
                .map(listingMapper::toDto)
                .toList();
    }

    public List<ListingResponseDTO> getListingsByOwner(Long ownerId) {
        return listingRepository.findByOwnerId(ownerId)
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

    public List<ListingResponseDTO> filterByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        List<Listing> listings = listingRepository.filterByPriceRange(minPrice, maxPrice);
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    //Filtrar por preço máximo
    public List<ListingResponseDTO> filterByMaxPrice(Double maxPrice) {
        if (maxPrice == null || maxPrice <= 0) {
            return getEnabledListings();
        }

        List<Listing> listings = listingRepository.findByDailyRateLessThanEqualAndEnabledTrue(maxPrice);
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    // Filtrar por preço mínimo
    public List<ListingResponseDTO> filterByMinPrice(Double minPrice) {
        if (minPrice == null || minPrice < 0) {
            return getEnabledListings();
        }

        List<Listing> listings = listingRepository.findByDailyRateGreaterThanEqualAndEnabledTrue(minPrice);
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return getEnabledListings();
        }

        List<Listing> listings = listingRepository.findByCityAndEnabledTrue(city.trim());
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<ListingResponseDTO> searchByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return getEnabledListings();
        }
        List<Listing> listings = listingRepository.findByCityContainingIgnoreCase(city.trim());
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<ListingResponseDTO> filterByDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            return getEnabledListings();
        }
        List<Listing> listings = listingRepository.findByDistrictAndEnabledTrue(district.trim());
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ListingResponseDTO> filterByPriceAndCity(Double minPrice, Double maxPrice, String city) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        List<Listing> listings = listingRepository.filterByPriceAndCity(minPrice, maxPrice, cityParam);
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<ListingResponseDTO> filterAdvanced(Double minPrice, Double maxPrice, String city, String district) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String districtParam = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
        List<Listing> listings = listingRepository.filterAdvanced(minPrice, maxPrice, cityParam, districtParam);
        return listings.stream()
                .map(listingMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obter todas as cidades disponíveis (para dropdown no frontend)
    public List<String> getAllCities() {
        return listingRepository.findByEnabledTrue().stream()
                .map(Listing::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Obter todos os distritos disponíveis
    public List<String> getAllDistricts() {
        return listingRepository.findByEnabledTrue().stream()
                .map(Listing::getDistrict)
                .filter(district -> district != null && !district.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
