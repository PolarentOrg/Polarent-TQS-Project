package com.tqs.polarent.controller;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.service.ListingService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingController listingController;

    private ListingResponseDTO camera1;
    private ListingResponseDTO camera2;

    @BeforeEach
    void setUp() {
        camera1 = new ListingResponseDTO();
        camera1.setId(1L);
        camera1.setTitle("Canon EOS R5");
        camera1.setDailyRate(89.99);

        camera2 = new ListingResponseDTO();
        camera2.setId(2L);
        camera2.setTitle("Sony A7IV");
        camera2.setDailyRate(79.99);
    }

    @Test
    void whenGetEnabledListings_thenReturn200() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1, camera2);
        when(listingService.getEnabledListings()).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.getEnabledListings();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Canon EOS R5");
    }

    @Test
    void whenSearchListingsWithTerm_thenReturn200() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1);
        when(listingService.searchListings("canon")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("canon");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Canon EOS R5");
    }

    @Test
    void whenSearchListingsWithoutTerm_thenReturnAllEnabled() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1, camera2);
        when(listingService.searchListings(null)).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void whenSearchListingsWithEmptyTerm_thenReturnAllEnabled() {
        List<ListingResponseDTO> listings = Arrays.asList(camera1, camera2);
        when(listingService.searchListings("")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void whenSearchListingsNoMatches_thenReturnEmptyList() {
        List<ListingResponseDTO> listings = Arrays.asList();
        when(listingService.searchListings("drone")).thenReturn(listings);
        ResponseEntity<List<ListingResponseDTO>> response = listingController.searchListings("drone");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}