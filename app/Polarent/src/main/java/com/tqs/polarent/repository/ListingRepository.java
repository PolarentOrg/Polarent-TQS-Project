package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByEnabledTrue();
    List<Listing> findByOwnerId(Long ownerId);
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Listing> searchByTerm(@Param("searchTerm") String searchTerm);
}
