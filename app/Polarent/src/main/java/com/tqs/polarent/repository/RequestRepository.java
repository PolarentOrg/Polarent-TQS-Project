package com.tqs.polarent.repository;

import com.tqs.polarent.dto.RequestRequestDTO;
import com.tqs.polarent.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByListingId(Long listingId);
    List<Request> findByListingAndRequesterId(Long  listingId, Long requesterId);
}