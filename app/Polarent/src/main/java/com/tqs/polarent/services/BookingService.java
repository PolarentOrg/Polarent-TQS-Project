package com.tqs.polarent.services;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.*;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.mapper.BookingMapper;
import com.tqs.polarent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;


    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Booking booking = bookingMapper.toEntity(dto);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return bookingMapper.toDto(booking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    public List<BookingResponseDTO> getBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO updateBookingStatus(Long id, Status status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        booking.setStatus(status);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingResponseDTO cancelBooking(Long id) {
        return updateBookingStatus(id, Status.CANCELLED);
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new IllegalArgumentException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }

    public List<BookingResponseDTO> getBookingsByRequesterId(Long requesterId) {
        return bookingRepository.findByRequesterId(requesterId).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RenterDashboardDTO getRenterDashboard(Long renterId) {
        User user = userRepository.findById(renterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Booking> allBookings = bookingRepository.findByRequesterId(renterId);

        List<DashboardRentalDTO> detailedRentals = mapBookingsToDashboardRentals(allBookings);
        Map<Status, List<DashboardRentalDTO>> rentalsByStatus = detailedRentals.stream()
                .collect(Collectors.groupingBy(DashboardRentalDTO::getStatus));

        RenterDashboardDTO dashboard = new RenterDashboardDTO();
        dashboard.setUserId(renterId);
        dashboard.setUserName(user.getFirstName() + " " + user.getLastName());
        dashboard.setUserEmail(user.getEmail());
        dashboard.setActiveRentals(rentalsByStatus.getOrDefault(Status.PAID, new ArrayList<>()));
        dashboard.setPendingRentals(rentalsByStatus.getOrDefault(Status.PENDING, new ArrayList<>()));
        dashboard.setCompletedRentals(rentalsByStatus.getOrDefault(Status.COMPLETED, new ArrayList<>()));
        dashboard.setCancelledRentals(rentalsByStatus.getOrDefault(Status.CANCELLED, new ArrayList<>()));
        dashboard.setActiveRentalsCount(rentalsByStatus.getOrDefault(Status.PAID, new ArrayList<>()).size());
        dashboard.setPendingRentalsCount(rentalsByStatus.getOrDefault(Status.PENDING, new ArrayList<>()).size());
        dashboard.setCompletedRentalsCount(rentalsByStatus.getOrDefault(Status.COMPLETED, new ArrayList<>()).size());
        dashboard.setCancelledRentalsCount(rentalsByStatus.getOrDefault(Status.CANCELLED, new ArrayList<>()).size());
        dashboard.setTotalRentals(allBookings.size());
        dashboard.setTotalSpent(calculateTotalSpent(allBookings));
        dashboard.setMonthlySpent(calculateMonthlySpent(allBookings));
        dashboard.setRentalsThisMonth(countRentalsThisMonth(allBookings));

        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<DashboardRentalDTO> getRenterRentals(Long renterId, String status) {
        List<Booking> bookings = bookingRepository.findByRequesterId(renterId);

        // Filtrar por status se fornecido
        if (status != null && !status.trim().isEmpty()) {
            try {
                Status statusEnum = Status.valueOf(status.toUpperCase());
                bookings = bookings.stream()
                        .filter(b -> b.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status);
            }
        }

        return mapBookingsToDashboardRentals(bookings);
    }

    @Transactional(readOnly = true)
    public RenterDashboardDTO getRenterStats(Long renterId) {
        User user = userRepository.findById(renterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Booking> allBookings = bookingRepository.findByRequesterId(renterId);

        RenterDashboardDTO stats = new RenterDashboardDTO();
        stats.setUserId(renterId);
        stats.setUserName(user.getFirstName() + " " + user.getLastName());
        stats.setUserEmail(user.getEmail());
        stats.setTotalRentals(allBookings.size());
        stats.setTotalSpent(calculateTotalSpent(allBookings));

        // Contar por status
        Map<Status, Long> statusCounts = allBookings.stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));

        stats.setActiveRentalsCount(statusCounts.getOrDefault(Status.PAID, 0L).intValue());
        stats.setPendingRentalsCount(statusCounts.getOrDefault(Status.PENDING, 0L).intValue());
        stats.setCompletedRentalsCount(statusCounts.getOrDefault(Status.COMPLETED, 0L).intValue());
        stats.setCancelledRentalsCount(statusCounts.getOrDefault(Status.CANCELLED, 0L).intValue());

        stats.setMonthlySpent(calculateMonthlySpent(allBookings));
        stats.setRentalsThisMonth(countRentalsThisMonth(allBookings));

        return stats;
    }

    @Transactional(readOnly = true)
    public List<DashboardRentalDTO> getRenterDetailedBookings(Long renterId) {
        List<Booking> bookings = bookingRepository.findByRequesterId(renterId);
        return mapBookingsToDashboardRentals(bookings);
    }

    // ====== HELPER METHODS ======

    private List<DashboardRentalDTO> mapBookingsToDashboardRentals(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapBookingToDashboardRental)
                .collect(Collectors.toList());
    }

    private DashboardRentalDTO mapBookingToDashboardRental(Booking booking) {
        DashboardRentalDTO dto = new DashboardRentalDTO();
        dto.setBookingId(booking.getId());
        dto.setPrice(booking.getPrice());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getCreatedAt());

        // Buscar informações do request associado
        Request request = requestRepository.findById(booking.getRequestId())
                .orElseThrow(() -> new IllegalStateException("Associated request not found for booking ID: " + booking.getId()));

        // Buscar informações do listing
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new IllegalStateException("Listing not found for request ID: " + request.getId()));

        dto.setListingId(listing.getId());
        dto.setListingTitle(listing.getTitle());

        // Buscar informações do owner
        User owner = userRepository.findById(listing.getOwnerId())
                .orElseThrow(() -> new IllegalStateException("Owner not found for listing ID: " + listing.getId()));

        dto.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
        dto.setOwnerEmail(owner.getEmail());
        dto.setStartDate(calculateStartDate(request));
        dto.setEndDate(calculateEndDate(request));

        return dto;
    }

    private LocalDateTime calculateStartDate(Request request) {
        return LocalDateTime.now().plusDays(request.getInitialDate());
    }

    private LocalDateTime calculateEndDate(Request request) {
        return calculateStartDate(request).plusDays(request.getDuration());
    }

    private Double calculateTotalSpent(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStatus() != Status.CANCELLED)
                .mapToDouble(Booking::getPrice)
                .sum();
    }

    private Double calculateMonthlySpent(List<Booking> bookings) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        return bookings.stream()
                .filter(b -> b.getStatus() != Status.CANCELLED)
                .filter(b -> b.getCreatedAt().isAfter(oneMonthAgo))
                .mapToDouble(Booking::getPrice)
                .sum();
    }

    private Integer countRentalsThisMonth(List<Booking> bookings) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        return (int) bookings.stream()
                .filter(b -> b.getCreatedAt().isAfter(oneMonthAgo))
                .count();
    }
}
