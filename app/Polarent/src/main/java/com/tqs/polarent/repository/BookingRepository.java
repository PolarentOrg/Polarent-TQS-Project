package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b JOIN Request r ON b.requestId = r.id WHERE r.requesterId = :requesterId")
    List<Booking> findByRequesterId(@Param("requesterId") Long requesterId);
}
