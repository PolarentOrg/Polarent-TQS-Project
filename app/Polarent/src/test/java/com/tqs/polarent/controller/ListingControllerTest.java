package com.tqs.polarent.controller;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingController listingController;

    private ListingResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ListingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOwnerId(10L);
        responseDTO.setTitle("Test Listing");
        responseDTO.setDescription("Description");
        responseDTO.setDailyRate(50.0);
        responseDTO.setEnabled(true);
    }

    @Test
    void whenGetEnabledListings_thenReturn200() {
        when(listingService.getEnabledListings()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<ListingResponseDTO>> response = listingController.getEnabledListings();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void whenUpdateListing_thenReturn200() {
        when(listingService.updateListing(eq(1L), any(ListingResponseDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ListingResponseDTO> response = listingController.updateListing(1L, responseDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Listing");
    }

    @Test
    void whenPatchListing_thenReturn200() {
        when(listingService.patchListing(eq(1L), any(ListingResponseDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ListingResponseDTO> response = listingController.patchListing(1L, responseDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Listing");
    }
}
