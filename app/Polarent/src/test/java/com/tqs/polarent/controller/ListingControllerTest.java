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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingController listingController;

    private ListingResponseDTO responseDTO;
    private ListingResponseDTO camera1Dto;
    private ListingResponseDTO camera2Dto;

    @BeforeEach
    void setUp() {
        responseDTO = new ListingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOwnerId(10L);
        responseDTO.setTitle("Test Listing");
        responseDTO.setDescription("Description");
        responseDTO.setDailyRate(50.0);
        responseDTO.setEnabled(true);

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
    void whenSearchListingsWithTerm_thenReturn200() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1Dto);
        when(listingService.searchListings("canon")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("canon");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Canon EOS R5");
    }

    @Test
    void whenSearchListingsWithoutTerm_thenReturnAllEnabled() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1Dto, camera2Dto);
        when(listingService.searchListings(null)).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void whenSearchListingsWithEmptyTerm_thenReturnAllEnabled() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1Dto, camera2Dto);
        when(listingService.searchListings("")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void whenPatchListing_thenReturn200() {
        when(listingService.patchListing(eq(1L), any(ListingResponseDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ListingResponseDTO> response = listingController.patchListing(1L, responseDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Listing");
    }

    @Test
    void whenSearchListingsNoMatches_thenReturnEmptyList() {
        List<ListingResponseDTO> listings = Arrays.asList();
        when(listingService.searchListings("drone")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("drone");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void whenDeleteListing_thenReturn204() {
        doNothing().when(listingService).deleteListing(10L, 1L);

        ResponseEntity<Void> response = listingController.deleteListing(10L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(listingService).deleteListing(10L, 1L);
    }
}