package com.tqs.polarent.services;

import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.dto.BookingResponseDTO;
import com.tqs.polarent.entity.Booking;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.mapper.BookingMapper;
import com.tqs.polarent.repository.BookingRepository;
import com.tqs.polarent.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final BookingMapper bookingMapper;

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
}
