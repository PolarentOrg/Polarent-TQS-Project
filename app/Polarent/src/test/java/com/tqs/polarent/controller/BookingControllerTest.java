package com.tqs.polarent.controller;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.services.BookingService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new BookingRequestDTO();
        requestDTO.setRequestId(1L);
        requestDTO.setPrice(250.0);
        requestDTO.setStatus(Status.PENDING);

        responseDTO = new BookingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRequestId(1L);
        responseDTO.setPrice(250.0);
        responseDTO.setStatus(Status.PENDING);
    }

    @Test
    void whenCreateBooking_thenReturn201() {
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<BookingResponseDTO> response = bookingController.createBooking(requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void whenGetBookingById_thenReturn200() {
        when(bookingService.getBookingById(1L)).thenReturn(responseDTO);

        ResponseEntity<BookingResponseDTO> response = bookingController.getBookingById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void whenGetAllBookings_thenReturn200() {
        when(bookingService.getAllBookings()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<BookingResponseDTO>> response = bookingController.getAllBookings();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void whenGetBookingsByRequesterId_thenReturn200() {
        when(bookingService.getBookingsByRequesterId(2L)).thenReturn(List.of(responseDTO));

        ResponseEntity<List<BookingResponseDTO>> response = bookingController.getBookingsByRequesterId(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void whenUpdateBookingStatus_thenReturn200() {
        responseDTO.setStatus(Status.PAID);
        when(bookingService.updateBookingStatus(eq(1L), eq(Status.PAID))).thenReturn(responseDTO);

        ResponseEntity<BookingResponseDTO> response = bookingController.updateBookingStatus(1L, Status.PAID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    void whenCancelBooking_thenReturn200() {
        responseDTO.setStatus(Status.CANCELLED);
        when(bookingService.cancelBooking(1L)).thenReturn(responseDTO);

        ResponseEntity<BookingResponseDTO> response = bookingController.cancelBooking(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    void whenDeleteBooking_thenReturn204() {
        doNothing().when(bookingService).deleteBooking(1L);

        ResponseEntity<Void> response = bookingController.deleteBooking(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(bookingService).deleteBooking(1L);
    }
}
