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
}
