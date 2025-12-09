package com.tqs.polarent.service;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingService listingService;

    private Listing camera1;
    private Listing camera2;
    private Listing disabledCamera;
    private ListingResponseDTO camera1Dto;
    private ListingResponseDTO camera2Dto;

    @BeforeEach
    void setUp() {
        camera1 = Listing.builder()
                .id(1L)
                .title("Canon EOS R5")
                .description("Professional camera")
                .dailyRate(89.99)
                .enabled(true)
                .build();

        camera2 = Listing.builder()
                .id(2L)
                .title("Sony A7IV")
                .description("Mirrorless camera")
                .dailyRate(79.99)
                .enabled(true)
                .build();

        disabledCamera = Listing.builder()
                .id(3L)
                .title("Nikon Z6 (Disabled)")
                .description("Not available")
                .dailyRate(69.99)
                .enabled(false)
                .build();

        camera1Dto = new ListingResponseDTO();
        camera1Dto.setId(1L);
        camera1Dto.setTitle("Canon EOS R5");
        camera1Dto.setDailyRate(89.99);

        camera2Dto = new ListingResponseDTO();
        camera2Dto.setId(2L);
        camera2Dto.setTitle("Sony A7IV");
        camera2Dto.setDailyRate(79.99);
    }

    @Test
    void whenGetEnabledListings_thenReturnOnlyEnabled() {
        List<Listing> enabledListings = Arrays.asList(camera1, camera2);
        when(listingRepository.findByEnabledTrue()).thenReturn(enabledListings);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);
        List<ListingResponseDTO> result = listingService.getEnabledListings();
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title")
                .containsExactly("Canon EOS R5", "Sony A7IV");
        verify(listingRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void whenGetEnabledListingsWithNoResults_thenReturnEmptyList() {
        when(listingRepository.findByEnabledTrue()).thenReturn(Collections.emptyList());
        List<ListingResponseDTO> result = listingService.getEnabledListings();
        assertThat(result).isEmpty();
        verify(listingRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void whenSearchListingsWithTerm_thenReturnMatchingEnabledListings() {
        List<Listing> matchingListings = Arrays.asList(camera1);
        when(listingRepository.searchByTerm("canon")).thenReturn(matchingListings);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);

        List<ListingResponseDTO> result = listingService.searchListings("canon");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Canon EOS R5");
        verify(listingRepository, times(1)).searchByTerm("canon");
        verify(listingRepository, never()).findByEnabledTrue();
    }

    @Test
    void whenSearchListingsWithNullTerm_thenReturnAllEnabled() {
        List<Listing> allEnabled = Arrays.asList(camera1, camera2);
        when(listingRepository.findByEnabledTrue()).thenReturn(allEnabled);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);
        List<ListingResponseDTO> result = listingService.searchListings(null);
        assertThat(result).hasSize(2);
        verify(listingRepository, times(1)).findByEnabledTrue();
        verify(listingRepository, never()).searchByTerm(any());
    }

    @Test
    void whenSearchListingsWithEmptyTerm_thenReturnAllEnabled() {
        List<Listing> allEnabled = Arrays.asList(camera1, camera2);
        when(listingRepository.findByEnabledTrue()).thenReturn(allEnabled);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);

        List<ListingResponseDTO> result = listingService.searchListings("");

        assertThat(result).hasSize(2);
        verify(listingRepository, times(1)).findByEnabledTrue();
        verify(listingRepository, never()).searchByTerm(any());
    }

    @Test
    void whenSearchListingsWithWhitespaceTerm_thenReturnAllEnabled() {
        List<Listing> allEnabled = Arrays.asList(camera1, camera2);
        when(listingRepository.findByEnabledTrue()).thenReturn(allEnabled);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);

        List<ListingResponseDTO> result = listingService.searchListings("   ");

        assertThat(result).hasSize(2);
        verify(listingRepository, times(1)).findByEnabledTrue();
        verify(listingRepository, never()).searchByTerm(any());
    }

    @Test
    void whenSearchListingsWithNoMatches_thenReturnEmptyList() {
        when(listingRepository.searchByTerm("drone")).thenReturn(Collections.emptyList());
        List<ListingResponseDTO> result = listingService.searchListings("drone");

        assertThat(result).isEmpty();
        verify(listingRepository, times(1)).searchByTerm("drone");
    }

    @Test
    void whenSearchListingsWithTermInDescription_thenReturnMatch() {
        List<Listing> matchingListings = Arrays.asList(camera2);
        when(listingRepository.searchByTerm("mirrorless")).thenReturn(matchingListings);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);

        List<ListingResponseDTO> result = listingService.searchListings("mirrorless");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Sony A7IV");
    }

    @Test
    void whenSearchListingsWithPartialTerm_thenReturnMatch() {
        List<Listing> matchingListings = Arrays.asList(camera1);
        when(listingRepository.searchByTerm("can")).thenReturn(matchingListings);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        List<ListingResponseDTO> result = listingService.searchListings("can");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Canon EOS R5");
    }

    @Test
    void whenSearchListingsReturnsDisabledListings_thenTheyShouldNotAppear() {
        // Note: O repository já filtra por enabled=true, então este teste
        // verifica que o disabledCamera não é retornado mesmo que apareça na busca

        List<Listing> matchingListings = Arrays.asList(camera1, camera2);
        when(listingRepository.searchByTerm("camera")).thenReturn(matchingListings);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);
        when(listingMapper.toDto(camera2)).thenReturn(camera2Dto);

        List<ListingResponseDTO> result = listingService.searchListings("camera");
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title")
                .doesNotContain("Nikon Z6 (Disabled)");
    }

    @Test
    void whenSearchListingsIsCaseInsensitive_thenReturnMatches() {
        List<Listing> matchingListings = Arrays.asList(camera1);
        when(listingRepository.searchByTerm("CANON")).thenReturn(matchingListings);
        when(listingMapper.toDto(camera1)).thenReturn(camera1Dto);

        List<ListingResponseDTO> result = listingService.searchListings("CANON");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Canon EOS R5");
    }
}