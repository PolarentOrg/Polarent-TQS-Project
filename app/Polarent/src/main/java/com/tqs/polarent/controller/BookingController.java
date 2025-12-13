package com.tqs.polarent.controller;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.tqs.polarent.dto.DashboardRentalDTO;
import com.tqs.polarent.dto.RenterDashboardDTO;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/renter/{requesterId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByRequesterId(@PathVariable Long requesterId) {
        return ResponseEntity.ok(bookingService.getBookingsByRequesterId(requesterId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(bookingService.getBookingsByOwnerId(ownerId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(@PathVariable Long id, @RequestParam Status status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<BookingResponseDTO> declineBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.declineBooking(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/renter/{renterId}/dashboard")
    public ResponseEntity<RenterDashboardDTO> getRenterDashboard(@PathVariable Long renterId) {
        return ResponseEntity.ok(bookingService.getRenterDashboard(renterId));
    }

    @GetMapping("/renter/{renterId}/rentals")
    public ResponseEntity<List<DashboardRentalDTO>> getRenterRentals(
            @PathVariable Long renterId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getRenterRentals(renterId, status));
    }

    @GetMapping("/renter/{renterId}/stats")
    public ResponseEntity<RenterDashboardDTO> getRenterStats(@PathVariable Long renterId) {
        return ResponseEntity.ok(bookingService.getRenterStats(renterId));
    }

    @GetMapping("/renter/{renterId}/detailed")
    public ResponseEntity<List<DashboardRentalDTO>> getRenterDetailedBookings(@PathVariable Long renterId) {
        return ResponseEntity.ok(bookingService.getRenterDetailedBookings(renterId));
    }
}
