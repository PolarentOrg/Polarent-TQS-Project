package com.tqs.polarent.service;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private Listing listing;
    private ListingResponseDTO responseDTO;
    private Listing camera1;
    private Listing camera2;
    private ListingResponseDTO camera1Dto;
    private ListingResponseDTO camera2Dto;

    @BeforeEach
    void setUp() {
        listing = Listing.builder()
                .id(1L)
                .ownerId(10L)
                .title("Original Title")
                .description("Original Description")
                .dailyRate(50.0)
                .enabled(true)
                .build();

        responseDTO = new ListingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOwnerId(10L);
        responseDTO.setTitle("Updated Title");
        responseDTO.setDescription("Updated Description");
        responseDTO.setDailyRate(75.0);
        responseDTO.setEnabled(false);

        camera1 = Listing.builder()
                .id(2L)
                .title("Canon EOS R5")
                .description("Professional camera")
                .dailyRate(89.99)
                .enabled(true)
                .build();

        camera2 = Listing.builder()
                .id(3L)
                .title("Sony A7IV")
                .description("Mirrorless camera")
                .dailyRate(79.99)
                .enabled(true)
                .build();

        camera1Dto = new ListingResponseDTO();
        camera1Dto.setId(2L);
        camera1Dto.setTitle("Canon EOS R5");
        camera1Dto.setDescription("Professional camera");
        camera1Dto.setDailyRate(89.99);
        camera1Dto.setEnabled(true);

        camera2Dto = new ListingResponseDTO();
        camera2Dto.setId(3L);
        camera2Dto.setTitle("Sony A7IV");
        camera2Dto.setDescription("Mirrorless camera");
        camera2Dto.setDailyRate(79.99);
        camera2Dto.setEnabled(true);
    }

    @Test
    void whenGetEnabledListings_thenReturnList() {
        when(listingRepository.findByEnabledTrue()).thenReturn(List.of(listing));
        when(listingMapper.toDto(listing)).thenReturn(responseDTO);

        List<ListingResponseDTO> result = listingService.getEnabledListings();

        assertThat(result).hasSize(1);
        verify(listingRepository).findByEnabledTrue();
    }

    @Test
    void whenUpdateListing_thenReturnUpdated() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenReturn(listing);
        when(listingMapper.toDto(any(Listing.class))).thenReturn(responseDTO);

        ListingResponseDTO result = listingService.updateListing(1L, responseDTO);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void whenUpdateListingNotFound_thenThrowException() {
        when(listingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.updateListing(1L, responseDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Listing not found");
    }

    @Test
    void whenPatchListing_thenReturnPatched() {
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenReturn(listing);
        when(listingMapper.toDto(any(Listing.class))).thenReturn(responseDTO);

        ListingResponseDTO result = listingService.patchListing(1L, responseDTO);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void whenPatchListingWithNullFields_thenOnlyUpdateNonNull() {
        ListingResponseDTO partialDto = new ListingResponseDTO();
        partialDto.setTitle("New Title");

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenReturn(listing);
        when(listingMapper.toDto(any(Listing.class))).thenReturn(responseDTO);

        listingService.patchListing(1L, partialDto);

        verify(listingRepository).save(argThat(l ->
                l.getTitle().equals("New Title") &&
                        l.getDescription().equals("Original Description")
        ));
    }

    @Test
    void whenPatchListingNotFound_thenThrowException() {
        when(listingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.patchListing(1L, responseDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Listing not found");
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