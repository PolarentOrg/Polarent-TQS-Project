package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;
import com.tqs.polarent.enums.Status;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b JOIN Request r ON b.requestId = r.id WHERE r.requesterId = :requesterId")
    List<Booking> findByRequesterId(@Param("requesterId") Long requesterId);

    @Query("SELECT b FROM Booking b JOIN Request r ON b.requestId = r.id JOIN Listing l ON r.listingId = l.id WHERE l.ownerId = :ownerId")
    List<Booking> findByOwnerId(@Param("ownerId") Long ownerId);

    // Consulta para bookings por status do renter
    @Query("SELECT b FROM Booking b JOIN Request r ON b.requestId = r.id " +
            "WHERE r.requesterId = :requesterId AND b.status = :status")
    List<Booking> findByRequesterIdAndStatus(
            @Param("requesterId") Long requesterId,
            @Param("status") Status status);

    // Consulta para gastos mensais
    @Query("SELECT SUM(b.price) FROM Booking b JOIN Request r ON b.requestId = r.id " +
            "WHERE r.requesterId = :renterId AND b.status != com.tqs.polarent.enums.Status.CANCELLED " +
            "AND b.createdAt >= :startDate")
    Double calculateMonthlySpent(
            @Param("renterId") Long renterId,
            @Param("startDate") LocalDateTime startDate);

    // Consulta para contagem de rentals deste mês
    @Query("SELECT COUNT(b) FROM Booking b JOIN Request r ON b.requestId = r.id " +
            "WHERE r.requesterId = :renterId AND b.createdAt >= :startDate")
    Integer countRentalsThisMonth(
            @Param("renterId") Long renterId,
            @Param("startDate") LocalDateTime startDate);
}
