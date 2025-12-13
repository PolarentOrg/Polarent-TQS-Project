package com.tqs.polarent.services;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.entity.Booking;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.mapper.BookingMapper;
import com.tqs.polarent.repository.BookingRepository;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.repository.UserRepository;
import com.tqs.polarent.dto.RenterDashboardDTO;
import com.tqs.polarent.dto.DashboardRentalDTO;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Request request;
    private Listing listing;
    private Booking booking;
    private BookingRequestDTO bookingRequestDTO;
    private BookingResponseDTO bookingResponseDTO;
    private User owner;
    private User renter;

    @BeforeEach
    void setUp() {
        listing = Listing.builder()
                .id(1L)
                .ownerId(1L)
                .title("Ski Equipment")
                .dailyRate(50.0)
                .enabled(true)
                .build();

        request = Request.builder()
                .id(1L)
                .listingId(1L)
                .requesterId(2L)
                .initialDate(20251210)
                .duration(5)
                .build();

        booking = Booking.builder()
                .id(1L)
                .requestId(1L)
                .price(250.0)
                .status(Status.PENDING)
                .build();

        bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setRequestId(1L);
        bookingRequestDTO.setPrice(250.0);
        bookingRequestDTO.setStatus(Status.PENDING);

        bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setId(1L);
        bookingResponseDTO.setRequestId(1L);
        bookingResponseDTO.setPrice(250.0);
        bookingResponseDTO.setStatus(Status.PENDING);
        booking.setCreatedAt(LocalDateTime.now().minusDays(5));
        
        owner = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        renter = User.builder()
                .id(2L)
                .firstName("Alice")
                .lastName("Renter")
                .email("renter@mail.com")
                .build();
    }

    @Test
    void whenCreateBooking_withValidRequest_thenReturnBooking() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(bookingMapper.toEntity(bookingRequestDTO)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        BookingResponseDTO result = bookingService.createBooking(bookingRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(250.0);
        assertThat(result.getStatus()).isEqualTo(Status.PENDING);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withInvalidRequest_thenThrowException() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request not found");
    }

    @Test
    void whenGetBookingById_withValidId_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        BookingResponseDTO result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void whenGetBookingById_withInvalidId_thenThrowException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking not found");
    }

    @Test
    void whenGetAllBookings_thenReturnList() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        List<BookingResponseDTO> result = bookingService.getAllBookings();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void whenUpdateBookingStatus_withValidId_thenReturnUpdatedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(Status.PAID);
        when(bookingRepository.save(booking)).thenReturn(booking);
        bookingResponseDTO.setStatus(Status.PAID);
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        BookingResponseDTO result = bookingService.updateBookingStatus(1L, Status.PAID);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    void whenCancelBooking_withValidId_thenReturnCancelledBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(Status.CANCELLED);
        when(bookingRepository.save(booking)).thenReturn(booking);
        bookingResponseDTO.setStatus(Status.CANCELLED);
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        BookingResponseDTO result = bookingService.cancelBooking(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    void whenDeleteBooking_withValidId_thenDeleteSuccessfully() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(1L);

        bookingService.deleteBooking(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void whenDeleteBooking_withInvalidId_thenThrowException() {
        when(bookingRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.deleteBooking(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking not found");
    }

    @Test
    void whenGetBookingsByRequesterId_thenReturnBookings() {
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        List<BookingResponseDTO> result = bookingService.getBookingsByRequesterId(2L);

        assertThat(result).hasSize(1);
    }

    @Test
    void whenGetBookingsByOwnerId_thenReturnBookings() {
        when(bookingRepository.findByOwnerId(1L)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDTO);

        List<BookingResponseDTO> result = bookingService.getBookingsByOwnerId(1L);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByOwnerId(1L);
    }

    @Test
    void whenGetRenterDashboard_thenReturnStats() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(renter));
        Booking paidBooking = Booking.builder()
            .id(1L)
            .requestId(1L)
            .price(250.0)
            .status(Status.PAID) 
            .createdAt(LocalDateTime.now().minusDays(5))
            .build();
        
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(paidBooking));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    
        RenterDashboardDTO dashboard = bookingService.getRenterDashboard(2L);
    
        assertThat(dashboard.getTotalRentals()).isEqualTo(1);
        assertThat(dashboard.getTotalSpent()).isEqualTo(250.0);
        assertThat(dashboard.getActiveRentalsCount()).isEqualTo(1); 
    }

    @Test
    void whenGetRenterDashboardUserNotFound_thenThrow() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getRenterDashboard(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void whenGetRenterRentalsWithoutStatus_thenReturnAll() {
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(booking));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        List<DashboardRentalDTO> result =
                bookingService.getRenterRentals(2L, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void whenGetRenterRentalsWithInvalidStatus_thenThrow() {
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(booking));

        assertThatThrownBy(() ->
                bookingService.getRenterRentals(2L, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status value");
    }

    @Test
    void whenGetRenterStats_thenReturnStats() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(renter));
        Booking paidBooking = Booking.builder()
            .id(1L)
            .requestId(1L)
            .price(250.0)
            .status(Status.PAID)
            .createdAt(LocalDateTime.now().minusDays(5))
            .build();
        
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(paidBooking));
    
        RenterDashboardDTO stats = bookingService.getRenterStats(2L);
    
        assertThat(stats.getTotalRentals()).isEqualTo(1);
        assertThat(stats.getActiveRentalsCount()).isEqualTo(1);
    }

    @Test
    void whenGetRenterDetailedBookings_thenReturnList() {
        when(bookingRepository.findByRequesterId(2L)).thenReturn(List.of(booking));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        List<DashboardRentalDTO> result =
                bookingService.getRenterDetailedBookings(2L);

        assertThat(result).hasSize(1);
    }
}
