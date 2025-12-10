package com.tqs.polarent.controller;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.services.BookingService;
import com.tqs.polarent.services.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private RequestController requestController;

    private RequestResponseDTO requestDto;
    private Listing listing;
    private BookingResponseDTO bookingResponseDTO;

    @BeforeEach
    void setUp() {
        requestDto = new RequestResponseDTO();
        requestDto.setId(1L);
        requestDto.setListingId(10L);
        requestDto.setRequesterId(5L);
        requestDto.setDuration(3);

        listing = Listing.builder()
                .id(10L)
                .dailyRate(50.0)
                .build();

        bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setId(1L);
        bookingResponseDTO.setRequestId(1L);
        bookingResponseDTO.setPrice(150.0);
        bookingResponseDTO.setStatus(Status.PENDING);
    }

    @Test
    void whenGetRequestsByListing_thenReturn200() {
        when(requestService.getRequestsByListing(10L)).thenReturn(List.of(requestDto));

        ResponseEntity<List<RequestResponseDTO>> response = requestController.getRequestsByListing(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void whenGetRequestsByListing_withNoRequests_thenReturnEmptyList() {
        when(requestService.getRequestsByListing(99L)).thenReturn(List.of());

        ResponseEntity<List<RequestResponseDTO>> response = requestController.getRequestsByListing(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void whenConvertToBooking_thenReturn200() {
        when(listingRepository.findById(10L)).thenReturn(Optional.of(listing));
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(bookingResponseDTO);

        ResponseEntity<BookingResponseDTO> response = requestController.convertToBooking(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRequestId()).isEqualTo(1L);
        assertThat(response.getBody().getPrice()).isEqualTo(150.0);
    }

    @Test
    void whenConvertToBookingWithInvalidListing_thenThrowException() {
        when(listingRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestController.convertToBooking(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Listing not found");
    }
}
