package com.tqs.polarent.services;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.*;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.mapper.BookingMapper;
import com.tqs.polarent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ListingRepository listingRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private Request request;
    private Listing listing;
    private User user;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setRequestId(10L);
        booking.setPrice(250.0);
        booking.setStatus(Status.PAID);
        booking.setCreatedAt(LocalDateTime.now().minusDays(5));

        request = new Request();
        request.setId(10L);
        request.setListingId(20L);
        request.setRequesterId(1L);
        request.setInitialDate(1);
        request.setDuration(3);

        listing = new Listing();
        listing.setId(20L);
        listing.setOwnerId(30L);
        listing.setTitle("Canon EOS R5");

        user = new User();
        user.setId(30L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
    }

    @Test
    void whenGetRenterRentalsWithStatus_thenFilterCorrectly() {
        when(bookingRepository.findByRequesterId(1L))
                .thenReturn(List.of(booking));

        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(listingRepository.findById(20L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(30L)).thenReturn(Optional.of(user));

        List<DashboardRentalDTO> result =
                bookingService.getRenterRentals(1L, "PAID");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    void whenGetRenterRentalsWithInvalidStatus_thenThrowException() {
        when(bookingRepository.findByRequesterId(1L))
                .thenReturn(List.of(booking));

        assertThatThrownBy(() ->
                bookingService.getRenterRentals(1L, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status value");
    }

    @Test
    void whenGetRenterDashboard_thenReturnDashboardData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByRequesterId(1L))
                .thenReturn(List.of(booking));

        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(listingRepository.findById(20L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(30L)).thenReturn(Optional.of(user));

        RenterDashboardDTO dashboard =
                bookingService.getRenterDashboard(1L);

        assertThat(dashboard.getUserId()).isEqualTo(1L);
        assertThat(dashboard.getTotalRentals()).isEqualTo(1);
        assertThat(dashboard.getActiveRentalsCount()).isEqualTo(1);
        assertThat(dashboard.getTotalSpent()).isEqualTo(250.0);
    }

    @Test
    void whenGetRenterStats_thenReturnStatistics() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByRequesterId(1L))
                .thenReturn(List.of(booking));

        RenterDashboardDTO stats =
                bookingService.getRenterStats(1L);

        assertThat(stats.getTotalRentals()).isEqualTo(1);
        assertThat(stats.getActiveRentalsCount()).isEqualTo(1);
        assertThat(stats.getCancelledRentalsCount()).isEqualTo(0);
        assertThat(stats.getTotalSpent()).isEqualTo(250.0);
    }


    @Test
    void whenDeleteExistingBooking_thenDeleteSuccessfully() {
        when(bookingRepository.existsById(1L)).thenReturn(true);

        bookingService.deleteBooking(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void whenDeleteNonExistingBooking_thenThrowException() {
        when(bookingRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() ->
                bookingService.deleteBooking(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking not found");
    }
}
