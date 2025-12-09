package com.tqs.polarent.services;

import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.repository.ListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService listingService;

    @Test
    void whenDeleteListing_withValidOwner_thenSuccess() {
        Listing listing = Listing.builder().id(1L).ownerId(10L).build();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        listingService.deleteListing(1L, 10L);

        verify(listingRepository).delete(listing);
    }

    @Test
    void whenDeleteListing_withInvalidOwner_thenThrowException() {
        Listing listing = Listing.builder().id(1L).ownerId(10L).build();
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> listingService.deleteListing(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only the owner can delete this listing");

        verify(listingRepository, never()).delete(any());
    }

    @Test
    void whenDeleteListing_withNonExistentListing_thenThrowException() {
        when(listingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.deleteListing(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Listing not found");

        verify(listingRepository, never()).delete(any());
    }
}
