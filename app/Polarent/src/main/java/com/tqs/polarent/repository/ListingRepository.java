package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByEnabledTrue();
    List<Listing> findByOwnerId(Long ownerId);
}
