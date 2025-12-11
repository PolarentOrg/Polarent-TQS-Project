package com.tqs.polarent.services;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingService listingService;

    @Test
    void getAllListings_ReturnsList() {
        Listing listing = Listing.builder().id(1L).title("Camera").build();
        ListingResponseDTO dto = new ListingResponseDTO();
        dto.setId(1L);
        when(listingRepository.findAll()).thenReturn(List.of(listing));
        when(listingMapper.toDto(listing)).thenReturn(dto);

        List<ListingResponseDTO> result = listingService.getAllListings();

        assertThat(result).hasSize(1);
    }

    @Test
    void getListingsByOwner_ReturnsList() {
        Listing listing = Listing.builder().id(1L).ownerId(5L).build();
        ListingResponseDTO dto = new ListingResponseDTO();
        dto.setOwnerId(5L);
        when(listingRepository.findByOwnerId(5L)).thenReturn(List.of(listing));
        when(listingMapper.toDto(listing)).thenReturn(dto);

        List<ListingResponseDTO> result = listingService.getListingsByOwner(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnerId()).isEqualTo(5L);
    }

    @Test
    void whenDeleteListing_withValidOwner_thenSuccess() {
        Listing listing = Listing.builder().id(1L).ownerId(10L).build();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        listingService.deleteListing(10L, 1L);

        verify(listingRepository).delete(listing);
    }

    @Test
    void whenDeleteListing_withInvalidOwner_thenThrowException() {
        Listing listing = Listing.builder().id(1L).ownerId(10L).build();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> listingService.deleteListing(99L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not authorized to delete this listing");

        verify(listingRepository, never()).delete(any());
    }

    @Test
    void whenDeleteListing_withNonExistentListing_thenThrowException() {
        when(listingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.deleteListing(10L, 10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Listing not found");

        verify(listingRepository, never()).delete(any());
    }
}
