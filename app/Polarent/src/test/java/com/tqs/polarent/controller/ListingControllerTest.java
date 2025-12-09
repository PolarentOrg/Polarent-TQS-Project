package com.tqs.polarent.controller;

import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingController listingController;

    @Test
    void whenDeleteListing_thenReturn204() {
        doNothing().when(listingService).deleteListing(10L, 1L);

        ResponseEntity<Void> response = listingController.deleteListing(10L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(listingService).deleteListing(10L, 1L);
    }
}
