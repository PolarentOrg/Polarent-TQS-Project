package com.tqs.polarent.service;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceFilterTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingService listingService;

    private Listing cheapLisbonCamera;
    private Listing mediumPortoCamera;
    private Listing expensiveLisbonLens;
    private Listing midRangeCoimbraTripod;

    private ListingResponseDTO cheapLisbonCameraDto;
    private ListingResponseDTO mediumPortoCameraDto;
    private ListingResponseDTO expensiveLisbonLensDto;
    private ListingResponseDTO midRangeCoimbraTripodDto;

    @BeforeEach
    void setUp() {
        cheapLisbonCamera = Listing.builder()
                .id(1L)
                .title("Basic Lisbon Camera")
                .description("Entry level camera in Lisbon")
                .dailyRate(29.99)
                .city("Lisbon")
                .district("Centro")
                .enabled(true)
                .build();

        mediumPortoCamera = Listing.builder()
                .id(2L)
                .title("Mid-range Porto Camera")
                .description("Good DSLR in Porto")
                .dailyRate(59.99)
                .city("Porto")
                .district("Centro")
                .enabled(true)
                .build();

        expensiveLisbonLens = Listing.builder()
                .id(3L)
                .title("Professional Lisbon Lens")
                .description("High-end lens in Lisbon")
                .dailyRate(129.99)
                .city("Lisbon")
                .district("Alvalade")
                .enabled(true)
                .build();

        midRangeCoimbraTripod = Listing.builder()
                .id(4L)
                .title("Coimbra Tripod")
                .description("Tripod in Coimbra")
                .dailyRate(44.99)
                .city("Coimbra")
                .district("Coimbra Centro")
                .enabled(true)
                .build();

        cheapLisbonCameraDto = new ListingResponseDTO();
        cheapLisbonCameraDto.setId(1L);
        cheapLisbonCameraDto.setTitle("Basic Lisbon Camera");
        cheapLisbonCameraDto.setDailyRate(29.99);
        cheapLisbonCameraDto.setCity("Lisbon");
        cheapLisbonCameraDto.setDistrict("Centro");

        mediumPortoCameraDto = new ListingResponseDTO();
        mediumPortoCameraDto.setId(2L);
        mediumPortoCameraDto.setTitle("Mid-range Porto Camera");
        mediumPortoCameraDto.setDailyRate(59.99);
        mediumPortoCameraDto.setCity("Porto");
        mediumPortoCameraDto.setDistrict("Centro");

        expensiveLisbonLensDto = new ListingResponseDTO();
        expensiveLisbonLensDto.setId(3L);
        expensiveLisbonLensDto.setTitle("Professional Lisbon Lens");
        expensiveLisbonLensDto.setDailyRate(129.99);
        expensiveLisbonLensDto.setCity("Lisbon");
        expensiveLisbonLensDto.setDistrict("Alvalade");

        midRangeCoimbraTripodDto = new ListingResponseDTO();
        midRangeCoimbraTripodDto.setId(4L);
        midRangeCoimbraTripodDto.setTitle("Coimbra Tripod");
        midRangeCoimbraTripodDto.setDailyRate(44.99);
        midRangeCoimbraTripodDto.setCity("Coimbra");
        midRangeCoimbraTripodDto.setDistrict("Coimbra Centro");
    }

    @Test
    void whenFilterByPriceRange_thenReturnListingsWithinRange() {
        List<Listing> filteredListings = Arrays.asList(mediumPortoCamera, midRangeCoimbraTripod);
        when(listingRepository.filterByPriceRange(30.0, 100.0)).thenReturn(filteredListings);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);

        List<ListingResponseDTO> result = listingService.filterByPriceRange(30.0, 100.0);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("dailyRate").containsExactly(59.99, 44.99);
    }

    @Test
    void whenFilterByPriceRangeWithNullMin_thenReturnUpToMax() {
        List<Listing> filteredListings = Arrays.asList(cheapLisbonCamera, midRangeCoimbraTripod);
        when(listingRepository.filterByPriceRange(null, 50.0)).thenReturn(filteredListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByPriceRange(null, 50.0);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("dailyRate").containsExactly(29.99, 44.99);
    }

    @Test
    void whenFilterByPriceRangeWithNullMax_thenReturnFromMin() {
        List<Listing> filteredListings = Arrays.asList(expensiveLisbonLens);
        when(listingRepository.filterByPriceRange(100.0, null)).thenReturn(filteredListings);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        List<ListingResponseDTO> result = listingService.filterByPriceRange(100.0, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDailyRate()).isEqualTo(129.99);
    }

    @Test
    void whenFilterByPriceRangeWithBothNull_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.filterByPriceRange(null, null)).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByPriceRange(null, null);
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByPriceRangeWithInvalidRange_thenThrowException() {
        assertThatThrownBy(() -> listingService.filterByPriceRange(100.0, 50.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minimum price cannot be greater than maximum price");

        verify(listingRepository, never()).filterByPriceRange(any(), any());
    }

    @Test
    void whenFilterByMaxPrice_thenReturnListingsBelowOrEqual() {
        List<Listing> filteredListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                midRangeCoimbraTripod);
        when(listingRepository.findByDailyRateLessThanEqualAndEnabledTrue(60.0)).thenReturn(filteredListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);

        List<ListingResponseDTO> result = listingService.filterByMaxPrice(60.0);

        assertThat(result).hasSize(3);
        assertThat(result).extracting("dailyRate").containsExactly(29.99, 59.99, 44.99);
    }

    @Test
    void whenFilterByMinPrice_thenReturnListingsAboveOrEqual() {
        List<Listing> filteredListings = Arrays.asList(mediumPortoCamera, expensiveLisbonLens);
        when(listingRepository.findByDailyRateGreaterThanEqualAndEnabledTrue(50.0)).thenReturn(filteredListings);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        List<ListingResponseDTO> result = listingService.filterByMinPrice(50.0);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("dailyRate").containsExactly(59.99, 129.99);
    }
    @Test
    void whenFilterByCity_thenReturnListingsFromCity() {
        List<Listing> lisbonListings = Arrays.asList(cheapLisbonCamera, expensiveLisbonLens);
        when(listingRepository.findByCityAndEnabledTrue("Lisbon")).thenReturn(lisbonListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);

        List<ListingResponseDTO> result = listingService.filterByCity("Lisbon");
         assertThat(result).hasSize(2);
         assertThat(result).extracting("city").containsOnly("Lisbon");
    }

    @Test
    void whenSearchByCityPartialMatch_thenReturnMatches() {
        List<Listing> matchingListings = Arrays.asList(cheapLisbonCamera, expensiveLisbonLens);
        when(listingRepository.findByCityContainingIgnoreCase("lis")).thenReturn(matchingListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        List<ListingResponseDTO> result = listingService.searchByCity("lis");
        assertThat(result).hasSize(2);
        assertThat(result).extracting("city").containsOnly("Lisbon");
    }

    @Test
    void whenFilterByDistrict_thenReturnListingsFromDistrict() {
        List<Listing> centroListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera);
        when(listingRepository.findByDistrictAndEnabledTrue("Centro")).thenReturn(centroListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);

        List<ListingResponseDTO> result = listingService.filterByDistrict("Centro");

        assertThat(result).hasSize(2);
        assertThat(result).extracting("district").containsOnly("Centro");
    }

    @Test
    void whenFilterByPriceAndCity_thenReturnFilteredListings() {
        List<Listing> filteredListings = Arrays.asList(cheapLisbonCamera);
        when(listingRepository.filterByPriceAndCity(20.0, 90.0, "Lisbon")).thenReturn(filteredListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);

        List<ListingResponseDTO> result = listingService.filterByPriceAndCity(20.0, 90.0, "Lisbon");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Lisbon");
        assertThat(result.get(0).getDailyRate()).isBetween(20.0, 90.0);
    }

    @Test
    void whenFilterAdvanced_thenReturnFilteredListings() {
        List<Listing> filteredListings = Arrays.asList(cheapLisbonCamera);
        when(listingRepository.filterAdvanced(20.0, 40.0, "Lisbon", "Centro")).thenReturn(filteredListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);

        List<ListingResponseDTO> result = listingService.filterAdvanced(20.0, 40.0, "Lisbon", "Centro");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Lisbon");
        assertThat(result.get(0).getDistrict()).isEqualTo("Centro");
        assertThat(result.get(0).getDailyRate()).isBetween(20.0, 40.0);
    }

    @Test
    void whenGetAllCities_thenReturnUniqueSortedCities() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        List<String> result = listingService.getAllCities();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Coimbra", "Lisbon", "Porto");
    }

    @Test
    void whenGetAllDistricts_thenReturnUniqueSortedDistricts() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        List<String> result = listingService.getAllDistricts();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Alvalade", "Centro", "Coimbra Centro");
    }

    @Test
    void whenFilterByCityWithNull_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByCity(null);
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByPriceRangeNoMatches_thenReturnEmptyList() {
        when(listingRepository.filterByPriceRange(200.0, 300.0)).thenReturn(Collections.emptyList());
        List<ListingResponseDTO> result = listingService.filterByPriceRange(200.0, 300.0);
        assertThat(result).isEmpty();
    }

    @Test
    void whenFilterByMaxPriceWithNull_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByMaxPrice(null);
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByEmptyString_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByCity("");
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByWhitespace_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByCity("   ");
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByMaxPriceWithZero_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByMaxPrice(0.0);
        assertThat(result).hasSize(4);
    }

    @Test
    void whenFilterByMinPriceWithNegative_thenReturnAllEnabled() {
        List<Listing> allListings = Arrays.asList(cheapLisbonCamera, mediumPortoCamera,
                expensiveLisbonLens, midRangeCoimbraTripod);
        when(listingRepository.findByEnabledTrue()).thenReturn(allListings);
        when(listingMapper.toDto(cheapLisbonCamera)).thenReturn(cheapLisbonCameraDto);
        when(listingMapper.toDto(mediumPortoCamera)).thenReturn(mediumPortoCameraDto);
        when(listingMapper.toDto(expensiveLisbonLens)).thenReturn(expensiveLisbonLensDto);
        when(listingMapper.toDto(midRangeCoimbraTripod)).thenReturn(midRangeCoimbraTripodDto);
        List<ListingResponseDTO> result = listingService.filterByMinPrice(-10.0);
        assertThat(result).hasSize(4);
    }
}